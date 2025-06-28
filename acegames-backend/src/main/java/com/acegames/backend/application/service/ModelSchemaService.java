package com.acegames.backend.application.service;
import com.acegames.backend.application.dto.ModelSchemaDto;
import com.acegames.backend.application.dto.ModelSchemaRegisterRequest;

import java.util.Map;
import java.util.Optional;
import java.util.List;


public interface ModelSchemaService {

    void register(ModelSchemaRegisterRequest request);
    List<ModelSchemaDto> getAllSchemas();
    Optional<ModelSchemaDto> getSchema(String collectionName);
    void generateFromClassName(String className);
    void deleteByCollectionName(String collection);
    
    // Yeni method: Referans kontrolü
    Map<String, List<String>> validateReferences(String className);

}
