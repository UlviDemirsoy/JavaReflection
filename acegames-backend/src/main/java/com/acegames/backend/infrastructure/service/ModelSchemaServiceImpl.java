package com.acegames.backend.infrastructure.service;

import com.acegames.backend.application.dto.FieldDefinition;
import com.acegames.backend.application.dto.ModelSchemaRegisterRequest;
import com.acegames.backend.application.helper.ReflectionSchemaParser;
import com.acegames.backend.application.service.ModelSchemaService;
import com.acegames.backend.application.dto.ModelSchemaDto;
import com.acegames.backend.infrastructure.model.ModelSchemaDocument;
import com.acegames.backend.infrastructure.repository.ModelSchemaRepository;
import com.acegames.backend.web.exception.DuplicateSchemaException;
import com.acegames.backend.web.exception.ReflectionException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;
import java.util.ArrayList;

@Service
public class ModelSchemaServiceImpl implements ModelSchemaService {

    private final ModelSchemaRepository repository;

    public ModelSchemaServiceImpl(ModelSchemaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void register(ModelSchemaRegisterRequest request) {
        // Duplicate kontrolü
        Optional<ModelSchemaDocument> existing = repository.findByCollection(request.getCollection());
        if (existing.isPresent()) {
            throw new DuplicateSchemaException(request.getCollection());
        }

        ModelSchemaDocument document = ModelSchemaDocument.builder()
                .collection(request.getCollection())
                .displayName(request.getDisplayName())
                .fields(request.getFields())
                .build();
        repository.save(document);
    }


    @Override
    public Optional<ModelSchemaDto> getSchema(String collection) {
        return repository.findByCollection(collection)
                .map(ModelSchemaDto::fromEntity); // fromEntity metodunu zaten yazmıştık
    }

    @Override
    public List<ModelSchemaDto> getAllSchemas() {
        return repository.findAll().stream()
                .map(ModelSchemaDto::fromEntity)
                .toList();
    }

    @Override
    public void generateFromClassName(String className) {
        try {
            Class<?> clazz = Class.forName("com.acegames.backend.domain.model." + className);

            String collection = className.toLowerCase(); // örnek: "cascade"
            String displayName = className;

            Map<String, FieldDefinition> fieldDefinitions = ReflectionSchemaParser.parseClass(clazz);

            Optional<ModelSchemaDocument> existingOpt = repository.findByCollection(collection);

            ModelSchemaDocument document = ModelSchemaDocument.builder()
                    .collection(collection)
                    .displayName(displayName)
                    .fields(fieldDefinitions)
                    .build();

            existingOpt.ifPresent(existing -> document.setId(existing.getId()));

            repository.save(document);

        } catch (ClassNotFoundException e) {
            throw new ReflectionException(className, "class_loading", "Class not found: " + className, e);
        } catch (Exception e) {
            throw new ReflectionException(className, "schema_generation", "Error generating schema for class: " + className, e);
        }
    }

    @Override
    public void deleteByCollectionName(String collection) {
        repository.deleteByCollection(collection);
    }

    @Override
    public Map<String, List<String>> validateReferences(String className) {
        try {
            Class<?> clazz = Class.forName("com.acegames.backend.domain.model." + className);
            Map<String, List<String>> validationResult = new LinkedHashMap<>();
            
            Map<String, FieldDefinition> fields = ReflectionSchemaParser.parseClass(clazz);
            
            for (Map.Entry<String, FieldDefinition> entry : fields.entrySet()) {
                FieldDefinition field = entry.getValue();
                if (field.getReference() != null) {
                    String referencedCollection = field.getReference();
                    
                    // Referans edilen collection var mı kontrol et
                    boolean exists = repository.findByCollection(referencedCollection).isPresent();
                    
                    List<String> issues = new ArrayList<>();
                    if (!exists) {
                        issues.add("Referenced collection '" + referencedCollection + "' does not exist");
                    }
                    
                    if (!issues.isEmpty()) {
                        validationResult.put(entry.getKey(), issues);
                    }
                }
            }
            
            return validationResult;
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(className, "class_loading", "Class not found: " + className, e);
        } catch (Exception e) {
            throw new ReflectionException(className, "reference_validation", "Error validating references for class: " + className, e);
        }
    }

}
