package com.acegames.backend.application.dto;

import lombok.Data;

import java.util.Map;

@Data
public class FieldDefinition {

    private String type;

    private String enumName;

    private Map<String, FieldDefinition> fields;

    private FieldDefinition items;
    private String reference;
}
