package com.example.srmsystem.controller;

import com.example.srmsystem.exception.AppException;
import com.example.srmsystem.exception.BadRequestException;
import com.example.srmsystem.exception.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logs")
@Slf4j
public class LogController {

    @Operation(
            summary = "Получить логи по дате",
            description = "Возвращает содержимое лог-файла за указанную дату. "
                    +
                    "Если дата равна текущей, используется файл logs/srmsystem.log, иначе - logs/srmsystem-<date>.log"
    )
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

    @Operation(
            summary = "Получить ограниченное количество логов по дате",
            description = "Возвращает последние N строк из лог-файла за указанную дату. "
                    +
                    "Полезно при просмотре последних событий без загрузки всего файла."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Логи успешно получены"),
            @ApiResponse(responseCode = "404", description = "Файл логов не найден"),
            @ApiResponse(responseCode = "400", description = "Некорректный параметр (например, отрицательное значение limit)"),
            @ApiResponse(responseCode = "500", description = "Ошибка при чтении логов")
    })
    @GetMapping("/limited")
    public ResponseEntity<String> getLogsByDateAndLimit(
            @Parameter(description = "Дата логов в формате YYYY-MM-DD", required = true, example = "2025-05-05")
            @RequestParam String date,

            @Parameter(description = "Максимальное количество последних строк логов", required = true, example = "100")
            @RequestParam int limit
    ) {

        if (limit <= 0) {
            throw new BadRequestException("Limit must be positive");
        }

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
            List<String> lines = Files.readAllLines(logFilePath);
            int fromIndex = Math.max(lines.size() - limit, 0);
            List<String> limitedLines = lines.subList(fromIndex, lines.size());

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"logs-" + date + "-limited.log\"")
                    .body(String.join("\n", limitedLines));

        } catch (IOException e) {
            log.error("Failed to read limited logs for date: {}", date, e);
            throw new AppException("Something went wrong when reading limited logs for date: " + date, e);
        }
    }
}
