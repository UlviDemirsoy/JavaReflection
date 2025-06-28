package com.acegames.backend.web.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private Map<String, Object> details;

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(status, error, message, path, LocalDateTime.now(), null);
    }

    public static ApiErrorResponse of(int status, String error, String message, String path, Map<String, Object> details) {
        return new ApiErrorResponse(status, error, message, path, LocalDateTime.now(), details);
    }
}
