package com.example.srmsystem.controller;

import com.example.srmsystem.dto.LogCreationStatusDto;
import com.example.srmsystem.enums.LogStatus;
import com.example.srmsystem.exception.AppException;
import com.example.srmsystem.exception.EntityNotFoundException;
import com.example.srmsystem.model.LogFile;
import com.example.srmsystem.service.AsyncLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/logs")
@Slf4j
public class LogController {

    private static final Path LOG_FILE_PATH = Paths.get("logs/srmsystem.log");
    private static final String LOG_DIRECTORY = "logs/";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    private final AsyncLogService asyncLogService;

    public LogController(AsyncLogService asyncLogService) {
        this.asyncLogService = asyncLogService;
    }

    @Operation(summary = "Получить логи по дате",
            description = "Получение логов, содержащих указанную дату, из файла логов.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Логи успешно получены"),
            @ApiResponse(responseCode = "204", description = "Логи не найдены для указанной даты"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос (например, неверный формат даты)"),
            @ApiResponse(responseCode = "500", description = "Ошибка при чтении файла логов")
    })
    @GetMapping
    public ResponseEntity<String> getLogsByDate(@RequestParam String date) {
        Path logFilePath;
        String today = java.time.LocalDate.now().toString();
        if (today.equals(date)) {
            logFilePath = Paths.get("logs/srmsystem.log");
        } else {
            logFilePath = Paths.get("logs/srmsystem-" + date + ".log");
        }
        if (!Files.exists(logFilePath)) {
            log.warn("Log file not found for date: {}", date);
            throw new EntityNotFoundException("Log file not found for date: " + date);
        }
        try {
            String content = Files.readString(logFilePath);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + logFilePath.getFileName() + "\"")
                    .body(content);
        } catch (IOException e) {
            log.error("Failed to read log file for date: {}", date, e);
            throw new AppException("Something went wrong when reading log file for date: " + date, e);
        }
    }

    @Operation(summary = "Создать лог файл асинхронно", description = "Создает лог файл асинхронно и возвращает ID задачи.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Задача на создание лог файла принята"),
            @ApiResponse(responseCode = "500", description = "Ошибка при создании задачи")
    })
    @PostMapping("/{date}")
    public CompletableFuture<ResponseEntity<String>> createLogFileAsync(
            @Parameter(description = "Дата лога в формате YYYY-MM-DD", example = "2023-10-27") @PathVariable String date
    ) {
        try {
            LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            return asyncLogService.createLogFileAsync(logDate)
                    .thenApply(logId -> ResponseEntity.status(HttpStatus.ACCEPTED).body(logId.toString()));
        } catch (Exception e) {
            throw new AppException("Error while creating log file for date: " + date, e);
        }
    }

    @Operation(summary = "Получить статус создания лог файла", description = "Возвращает статус создания лог файла по ID задачи.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно получен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    @GetMapping("/status/{logId}")
    public ResponseEntity<LogCreationStatusDto> getLogCreationStatus(
            @Parameter(description = "ID задачи на создание лог файла", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479") @PathVariable UUID logId
    ) {
        LogCreationStatusDto status = asyncLogService.getLogCreationStatus(logId);
        if (status.getStatus() == LogStatus.NOT_FOUND) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Log creation task not found");
        }
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Получить лог файл по дате и ротации", description = "Возвращает лог файл за указанную дату и номер ротации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лог файл успешно получен"),
            @ApiResponse(responseCode = "404", description = "Лог файл не найден")
    })
    @GetMapping("/{date}/rotation/{rotation}")
    public ResponseEntity<Resource> getLogFileByDateAndRotation(
            @Parameter(description = "Дата лога в формате YYYY-MM-DD", example = "2023-10-27") @PathVariable String date,
            @Parameter(description = "Номер ротации лога", example = "0") @PathVariable Integer rotation
    ) {
        try {
            LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            String logFileName = "app-" + logDate.format(DATE_FORMATTER) + "." + rotation + ".log";
            Path logFilePath = Paths.get(LOG_DIRECTORY, logFileName);

            if (!Files.exists(logFilePath)) {
                throw new EntityNotFoundException("Log file not found for date: " + date);
            }

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(logFilePath));
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + logFileName);
            headers.setContentType(MediaType.TEXT_PLAIN);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(Files.size(logFilePath))
                    .body(resource);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading log file", e);
        }
    }

    @Operation(summary = "Получить готовый лог файл по ID задачи", description = "Возвращает готовый лог файл по ID задачи.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Лог файл успешно получен"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена или лог файл не готов")
    })
    @GetMapping("/file/{logId}")
    public ResponseEntity<Resource> getLogFileByLogId(
            @Parameter(description = "ID задачи на создание лог файла", example = "f47ac10b-58cc-4372-a567-0e02b2c3d479") @PathVariable UUID logId
    ) {
        LogFile logFile = asyncLogService.getLogFile(logId);
        if (logFile == null || logFile.getStatus() != LogStatus.COMPLETED) {
            throw new EntityNotFoundException("Log file not ready or task not found");
        }

        String filePath = logFile.getFilePath();
        if (filePath == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File path not available");
        }

        File file = new File(filePath);
        if (!file.exists()) {
            throw new EntityNotFoundException("Log file not found");
        }

        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());
        headers.setContentType(MediaType.TEXT_PLAIN);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .body(resource);
    }
}