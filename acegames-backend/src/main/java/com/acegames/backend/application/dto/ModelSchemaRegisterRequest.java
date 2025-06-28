package com.acegames.backend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ModelSchemaRegisterRequest {

    @NotBlank(message = "Collection name cannot be blank")
    private String collection;

    @NotBlank(message = "Display name cannot be blank")
    private String displayName;

    @NotNull(message = "Fields cannot be null")
    private Map<String, FieldDefinition> fields;
}
