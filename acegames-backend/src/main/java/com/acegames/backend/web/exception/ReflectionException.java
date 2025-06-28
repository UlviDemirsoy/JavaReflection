package com.acegames.backend.web.exception;

public class ReflectionException extends RuntimeException {
    private final String className;
    private final String operation;

    public ReflectionException(String className, String operation, String message) {
        super(message);
        this.className = className;
        this.operation = operation;
    }

    public ReflectionException(String className, String operation, String message, Throwable cause) {
        super(message, cause);
        this.className = className;
        this.operation = operation;
    }

    public String getClassName() {
        return className;
    }

    public String getOperation() {
        return operation;
    }
} 