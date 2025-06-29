package com.acegames.backend.application.helper;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    
    private static final Random random = ThreadLocalRandom.current();
    
    public static Object generateSampleData(Class<?> clazz) {
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = generateValueForField(field);
                field.set(instance, value);
            }
            
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Error generating sample data for class: " + clazz.getName(), e);
        }
    }
    
    private static Object generateValueForField(Field field) {
        Class<?> fieldType = field.getType();
        String fieldName = field.getName().toLowerCase();
        
        // Handle special cases first
        if (fieldName.equals("_id") || fieldName.equals("id")) {
            return generateId();
        }
        
        if (fieldName.contains("date") || fieldName.contains("time")) {
            return generateDateValue(fieldType);
        }
        
        if (fieldName.contains("name")) {
            return generateNameValue(fieldName);
        }
        
        if (fieldName.contains("price")) {
            return generatePriceValue(fieldType);
        }
        
        if (fieldName.contains("currency")) {
            return generateCurrencyValue();
        }
        
        // Handle basic types
        if (fieldType == String.class) {
            return generateStringValue(fieldName);
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return generateIntegerValue(fieldName);
        } else if (fieldType == Long.class || fieldType == long.class) {
            return generateLongValue(fieldName);
        } else if (fieldType == Double.class || fieldType == double.class) {
            return generateDoubleValue(fieldName);
        } else if (fieldType == Boolean.class || fieldType == boolean.class) {
            return random.nextBoolean();
        } else if (fieldType.isEnum()) {
            return generateEnumValue(fieldType);
        } else if (List.class.isAssignableFrom(fieldType)) {
            return generateListValue(field);
        } else if (fieldType.getPackage() != null && 
                   fieldType.getPackage().getName().startsWith("com.acegames.backend.domain.model")) {
            // Handle nested model objects
            return generateSampleData(fieldType);
        }
        
        return null;
    }
    
    private static String generateId() {
        return UUID.randomUUID().toString();
    }
    
    private static Object generateDateValue(Class<?> fieldType) {
        long currentTime = System.currentTimeMillis();
        long randomOffset = random.nextLong(-30 * 24 * 60 * 60 * 1000L, 30 * 24 * 60 * 60 * 1000L); // ±30 days
        
        if (fieldType == Long.class || fieldType == long.class) {
            return currentTime + randomOffset;
        } else if (fieldType == Date.class) {
            return new Date(currentTime + randomOffset);
        } else if (fieldType == LocalDateTime.class) {
            return LocalDateTime.ofEpochSecond((currentTime + randomOffset) / 1000, 0, ZoneOffset.UTC);
        }
        
        return currentTime + randomOffset;
    }
    
    private static String generateNameValue(String fieldName) {
        String[] prefixes = {"Sample", "Test", "Demo", "Example", "Mock"};
        String[] suffixes = {"Item", "Product", "Offer", "Cascade", "Skin", "Tile"};
        
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        
        return prefix + " " + suffix + " " + (random.nextInt(100) + 1);
    }
    
    private static Object generatePriceValue(Class<?> fieldType) {
        double price = 1.0 + random.nextDouble() * 99.0; // 1.0 to 100.0
        
        if (fieldType == Double.class || fieldType == double.class) {
            return Math.round(price * 100.0) / 100.0; // Round to 2 decimal places
        } else if (fieldType == Integer.class || fieldType == int.class) {
            return (int) Math.round(price);
        }
        
        return price;
    }
    
    private static String generateCurrencyValue() {
        String[] currencies = {"USD", "EUR", "TRY", "GBP", "JPY"};
        return currencies[random.nextInt(currencies.length)];
    }
    
    private static String generateStringValue(String fieldName) {
        if (fieldName.contains("id")) {
            return "id_" + random.nextInt(1000);
        }
        return "sample_" + fieldName + "_" + random.nextInt(1000);
    }
    
    private static Integer generateIntegerValue(String fieldName) {
        if (fieldName.contains("id")) {
            return random.nextInt(1, 1000);
        }
        return random.nextInt(1, 100);
    }
    
    private static Long generateLongValue(String fieldName) {
        if (fieldName.contains("id")) {
            return random.nextLong(1, 1000);
        }
        return random.nextLong(1, 1000000);
    }
    
    private static Double generateDoubleValue(String fieldName) {
        return random.nextDouble() * 100.0;
    }
    
    private static Object generateEnumValue(Class<?> fieldType) {
        Object[] enumConstants = fieldType.getEnumConstants();
        if (enumConstants != null && enumConstants.length > 0) {
            return enumConstants[random.nextInt(enumConstants.length)];
        }
        return null;
    }
    
    private static List<Object> generateListValue(Field field) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType pt) {
            Type actualType = pt.getActualTypeArguments()[0];
            if (actualType instanceof Class<?> actualClass) {
                List<Object> list = new ArrayList<>();
                int listSize = random.nextInt(1, 4); // Generate 1-3 items
                
                for (int i = 0; i < listSize; i++) {
                    if (actualClass.getPackage() != null && 
                        actualClass.getPackage().getName().startsWith("com.acegames.backend.domain.model")) {
                        list.add(generateSampleData(actualClass));
                    } else {
                        list.add(generateValueForType(actualClass));
                    }
                }
                
                return list;
            }
        }
        return new ArrayList<>();
    }
    
    private static Object generateValueForType(Class<?> type) {
        if (type == String.class) {
            return "sample_" + random.nextInt(1000);
        } else if (type == Integer.class || type == int.class) {
            return random.nextInt(1, 100);
        } else if (type == Long.class || type == long.class) {
            return random.nextLong(1, 1000);
        } else if (type == Double.class || type == double.class) {
            return random.nextDouble() * 100.0;
        } else if (type == Boolean.class || type == boolean.class) {
            return random.nextBoolean();
        } else if (type.isEnum()) {
            return generateEnumValue(type);
        }
        
        return null;
    }
} 