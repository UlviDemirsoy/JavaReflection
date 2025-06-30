package com.acegames.backend.application.helper;

import java.util.HashMap;
import java.util.Map;

public class EnumScanner {

    private static final String ENUM_PACKAGE = "com.acegames.backend.domain.enum";

    public static Map<String, String[]> scanEnums() {
        Map<String, String[]> result = new HashMap<>();

        Class<?>[] enumClasses = new Class[]{
                com.acegames.backend.domain.enums.Requirement.class,
                com.acegames.backend.domain.enums.TradeType.class,
                com.acegames.backend.domain.enums.EventType.class
        };

        for (Class<?> enumClass : enumClasses) {
            if (enumClass.isEnum()) {
                Object[] constants = enumClass.getEnumConstants();
                String[] names = new String[constants.length];
                for (int i = 0; i < constants.length; i++) {
                    names[i] = constants[i].toString();
                }
                result.put(enumClass.getSimpleName(), names);
            }
        }

        return result;
    }
}
