package com.acegames.backend.infrastructure.model;

import com.acegames.backend.application.dto.FieldDefinition;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Document(collection = "schemas")
@Data
@Builder
public class ModelSchemaDocument {

    @Id
    private String id;

    private String collection;
    private String displayName;
    private Map<String, FieldDefinition> fields;
}
