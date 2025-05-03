package com.example.srmsystem.model;

import com.example.srmsystem.enums.LogStatus;
import lombok.Data;


@Data
public class LogFile {
    private String id;
    private LogStatus status;
    private String filePath;
    private String errorMessage;

    public LogFile(String id) {
        this.id = id;
        this.status = LogStatus.IN_PROGRESS;
    }
} 