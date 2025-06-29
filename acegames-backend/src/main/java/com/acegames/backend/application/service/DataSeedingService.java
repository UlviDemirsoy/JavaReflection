package com.acegames.backend.application.service;

import java.util.List;
import java.util.Map;

public interface DataSeedingService {
    
    /**
     * Seed data for a specific class using reflection
     * @param className The name of the class to seed data for
     * @param count Number of sample records to generate
     * @return Map containing the generated data
     */
    Map<String, Object> seedDataForClass(String className, int count);
    
    /**
     * Seed data for multiple classes
     * @param classNames List of class names to seed
     * @param countPerClass Number of sample records per class
     * @return Map with class names as keys and generated data as values
     */
    Map<String, Object> seedDataForClasses(List<String> classNames, int countPerClass);
    
    /**
     * Get available classes that can be seeded
     * @return List of available class names
     */
    List<String> getAvailableClasses();
    
    /**
     * Clear all seeded data for a specific class
     * @param className The name of the class to clear data for
     */
    void clearSeededData(String className);
} 