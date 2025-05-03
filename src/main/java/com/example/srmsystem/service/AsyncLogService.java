package com.example.srmsystem.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import com.example.srmsystem.dto.LogCreationStatusDto;
import com.example.srmsystem.enums.LogStatus;
import com.example.srmsystem.model.LogFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AsyncLogService {

    private static final String LOG_DIRECTORY = "logs/";
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    private final Map<String, LogFile> logFiles = new ConcurrentHashMap<>();

    @Async
    public CompletableFuture<String> createLogFileAsync(LocalDate date) {
        String logId = UUID.randomUUID().toString();
        LogFile logFile = new LogFile(logId);
        logFiles.put(logId, logFile);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(15000);

                String logFileNamePattern = "app-" + date.format(DATE_FORMATTER) + ".*.log";
                Path logDirectoryPath = Paths.get(LOG_DIRECTORY);

                if (!Files.exists(logDirectoryPath) || !Files.isDirectory(logDirectoryPath)) {
                    logFile.setStatus(LogStatus.FAILED);
                    logFile.setErrorMessage("Log directory not found");
                    log.error("Log directory not found");
                    return;
                }

                List<Path> matchingFiles = Files.list(logDirectoryPath)
                        .filter(path -> path.getFileName().toString()
                                .matches(logFileNamePattern.replace(".", "\\.")
                                        .replace("*", ".*")))
                        .toList();

                if (matchingFiles.isEmpty()) {
                    logFile.setStatus(LogStatus.FAILED);
                    logFile.setErrorMessage("Log files not found for date: " + date);
                    log.error("Log files not found for date: {}", date);
                    return;
                }

                String combinedLogFileName = "app-" + date.format(DATE_FORMATTER) + "-" + logId + ".log";
                Path combinedLogFilePath = Paths.get(LOG_DIRECTORY, combinedLogFileName);
                Files.createDirectories(combinedLogFilePath.getParent());

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(combinedLogFilePath.toFile()))) {
                    for (Path logFilePath : matchingFiles) {
                        List<String> lines = Files.readAllLines(logFilePath);
                        for (String line : lines) {
                            writer.write(line);
                            writer.newLine();
                        }
                    }
                }

                logFile.setStatus(LogStatus.COMPLETED);
                logFile.setFilePath(combinedLogFilePath.toString());
            } catch (IOException | InterruptedException e) {
                logFile.setStatus(LogStatus.FAILED);
                logFile.setErrorMessage(e.getMessage());
                log.error("Error creating log file", e);
            }
        });

        return CompletableFuture.completedFuture(logId);
    }

    public LogCreationStatusDto getLogCreationStatus(UUID logId) {
        LogFile logFile = logFiles.get(logId.toString());
        if (logFile == null) {
            return new LogCreationStatusDto(LogStatus.NOT_FOUND, "Log file not found");
        }
        return new LogCreationStatusDto(logFile.getStatus(), logFile.getErrorMessage());
    }

    public LogFile getLogFile(UUID logId) {
        return logFiles.get(logId.toString());
    }
}