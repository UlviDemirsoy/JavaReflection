package com.acegames.backend.application.dto;

import com.acegames.backend.infrastructure.model.ModelSchemaDocument;
import lombok.Data;

import java.util.Map;

@Data
public class ModelSchemaDto {
    private String collection;
    private String displayName;
    private Map<String, FieldDefinition> fields;

    public static ModelSchemaDto fromEntity(ModelSchemaDocument entity) {
        ModelSchemaDto dto = new ModelSchemaDto();
        dto.setCollection(entity.getCollection());
        dto.setDisplayName(entity.getDisplayName());
        dto.setFields(entity.getFields());
        return dto;
    }
}
