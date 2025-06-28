package com.acegames.backend.web.exception;

public class SchemaValidationException extends RuntimeException {
    private final String fieldName;
    private final String validationRule;

    public SchemaValidationException(String message) {
        super(message);
        this.fieldName = null;
        this.validationRule = null;
    }

    public SchemaValidationException(String fieldName, String validationRule, String message) {
        super(message);
        this.fieldName = fieldName;
        this.validationRule = validationRule;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValidationRule() {
        return validationRule;
    }
} 