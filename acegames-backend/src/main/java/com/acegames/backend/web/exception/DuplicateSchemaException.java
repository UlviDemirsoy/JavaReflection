package com.acegames.backend.web.exception;

public class DuplicateSchemaException extends RuntimeException {
    private final String collectionName;

    public DuplicateSchemaException(String collectionName) {
        super("Schema already exists for collection: " + collectionName);
        this.collectionName = collectionName;
    }

    public String getCollectionName() {
        return collectionName;
    }
} 