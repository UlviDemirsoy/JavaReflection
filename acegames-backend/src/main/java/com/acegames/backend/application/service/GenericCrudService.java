package com.acegames.backend.application.service;

import org.bson.Document;

import java.util.List;
import java.util.Map;

public interface GenericCrudService {
    List<Document> findAll(String collectionName);

    Document findById(String collectionName, String id);

    Document insert(String collectionName, Map<String, Object> data);

    Document update(String collectionName, String id, Map<String, Object> data);

    void delete(String collectionName, String id);
}
