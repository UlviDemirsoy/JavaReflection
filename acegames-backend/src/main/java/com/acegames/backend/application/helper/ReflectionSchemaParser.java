package com.acegames.backend.application.helper;

import com.acegames.backend.application.dto.FieldDefinition;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReflectionSchemaParser {

    public static Map<String, FieldDefinition> parseClass(Class<?> clazz) {
        Map<String, FieldDefinition> fields = new LinkedHashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            FieldDefinition def = new FieldDefinition();

            String fieldName = field.getName().toLowerCase();
            if ((fieldType.equals(Long.class) || fieldType.equals(long.class)) &&
                (fieldName.contains("date") || fieldName.contains("time") ||
                 fieldName.contains("created") || fieldName.contains("updated") ||
                 fieldName.contains("expire"))) {
                def.setType("Date");
            }
            else if (fieldType.isEnum()) {
                def.setType("Enum");
                def.setEnumName(fieldType.getSimpleName());
            } else if (List.class.isAssignableFrom(fieldType)) {
                def.setType("Array");

                Type genericType = field.getGenericType();
                if (genericType instanceof ParameterizedType pt) {
                    Type actualType = pt.getActualTypeArguments()[0];
                    if (actualType instanceof Class<?> actualClass) {
                        def.setItems(parseFieldType(actualClass));
                    }
                }

            } else if (isPrimitive(fieldType)) {
                def.setType(getPrimitiveType(fieldType));
            } else {
                def.setType("Object");
                def.setFields(parseClass(fieldType));
            }

            String reference = extractReferenceName(field.getName());
            if (reference != null) {
                def.setReference(reference);
            }

            fields.put(field.getName(), def);
        }

        return fields;
    }

    private static boolean isPrimitive(Class<?> type) {
        return type == String.class ||
                Number.class.isAssignableFrom(type) ||
                type == int.class || type == double.class || type == boolean.class ||
                type == Boolean.class || type == Integer.class || type == Double.class ||
                type == java.util.Date.class || type == java.time.LocalDate.class;
    }

    private static String getPrimitiveType(Class<?> type) {
        if (type == String.class) return "String";
        if (type == boolean.class || type == Boolean.class) return "Boolean";
        if (type == java.util.Date.class || type == java.time.LocalDate.class) return "Date";
        return "Number";
    }

    private static FieldDefinition parseFieldType(Class<?> clazz) {
        FieldDefinition def = new FieldDefinition();
        if (clazz.isEnum()) {
            def.setType("Enum");
            def.setEnumName(clazz.getSimpleName());
        } else if (isPrimitive(clazz)) {
            def.setType(getPrimitiveType(clazz));
        } else {
            def.setType("Object");
            def.setFields(parseClass(clazz));
        }
        return def;
    }

    private static String extractReferenceName(String fieldName) {
        if (fieldName.endsWith("Ids")) {
            return toCamelCase(fieldName.substring(0, fieldName.length() - 3));
        } else if (fieldName.endsWith("Id")) {
            return toCamelCase(fieldName.substring(0, fieldName.length() - 2));
        }
        return null;
    }

    private static String toCamelCase(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }
}
