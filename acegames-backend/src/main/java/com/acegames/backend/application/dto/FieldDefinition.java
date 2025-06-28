package com.acegames.backend.application.dto;

import lombok.Data;

import java.util.Map;

@Data
public class FieldDefinition {

    // Tip bilgisi: string, number, boolean, enum, date, object, array
    private String type;

    // Eğer enum ise, bu alan kullanılır
    private String enumName;

    // Eğer type = object ise, alt alanlar buraya tanımlanır
    private Map<String, FieldDefinition> fields;

    // Eğer type = array ise, bu dizideki elemanların tipi
    private FieldDefinition items;
    private String reference;
}
