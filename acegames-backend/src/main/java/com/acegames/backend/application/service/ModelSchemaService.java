package com.acegames.backend.application.service;

import com.acegames.backend.application.dto.ModelSchemaDto;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ModelSchemaService {
    Optional<ModelSchemaDto> getSchema(String collection);
    List<ModelSchemaDto> getAllSchemas();
    void generateFromClassName(String className);
    void deleteByCollectionName(String collection);
    
    // Yeni method: Referans kontrolü
    Map<String, List<String>> validateReferences(String className);
}
