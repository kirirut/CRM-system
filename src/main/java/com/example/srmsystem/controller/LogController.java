package com.example.srmsystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
@Slf4j
public class LogController {

    private static final Path LOG_FILE_PATH = Paths.get("logs/srmsystem.log");

    @GetMapping
    public ResponseEntity<String> getLogsByDate(@RequestParam String date) {
        try (BufferedReader reader = Files.newBufferedReader(LOG_FILE_PATH)) {
            List<String> filteredLogs = reader.lines()
                    .filter(line -> line.contains(date))
                    .collect(Collectors.toList());

            if (filteredLogs.isEmpty()) {
                log.warn("No logs found for date: {}", date);
                return ResponseEntity.notFound().build();
            }

            String result = String.join("\n", filteredLogs);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"logs-" + date + ".log\"")
                    .body(result);

        } catch (IOException e) {
            log.error("Failed to read log file", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
