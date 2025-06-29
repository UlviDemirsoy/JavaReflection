package com.acegames.backend.infrastructure.service;

import com.acegames.backend.application.helper.DataGenerator;
import com.acegames.backend.application.service.DataSeedingService;
import com.acegames.backend.web.exception.ReflectionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataSeedingServiceImpl implements DataSeedingService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSeedingServiceImpl.class);
    private static final String MODEL_PACKAGE = "com.acegames.backend.domain.model";
    
    // In-memory storage for seeded data (in a real application, this would be in a database)
    private final Map<String, List<Object>> seededData = new ConcurrentHashMap<>();
    
    // Cache of available classes - only the 4 specified classes in order
    private final List<String> availableClasses = Arrays.asList(
        "Skin", "PurchaseProduct", "Offer", "Cascade"
    );
    
    @Override
    public Map<String, Object> seedDataForClass(String className, int count) {
        try {
            logger.info("Starting data seeding for class: {} with {} records", className, count);
            
            Class<?> clazz = Class.forName(MODEL_PACKAGE + "." + className);
            List<Object> generatedData = new ArrayList<>();
            
            for (int i = 0; i < count; i++) {
                Object sampleData = DataGenerator.generateSampleData(clazz);
                generatedData.add(sampleData);
            }
            
            // Store the generated data
            seededData.put(className, generatedData);
            
            Map<String, Object> result = new HashMap<>();
            result.put("className", className);
            result.put("count", count);
            result.put("data", generatedData);
            result.put("message", String.format("Successfully generated %d sample records for %s", count, className));
            
            logger.info("Successfully seeded {} records for class: {}", count, className);
            return result;
            
        } catch (ClassNotFoundException e) {
            throw new ReflectionException(className, "class_loading", "Class not found: " + className, e);
        } catch (Exception e) {
            throw new ReflectionException(className, "data_seeding", "Error seeding data for class: " + className, e);
        }
    }
    
    @Override
    public Map<String, Object> seedDataForClasses(List<String> classNames, int countPerClass) {
        Map<String, Object> results = new HashMap<>();
        List<Map<String, Object>> classResults = new ArrayList<>();
        
        logger.info("Starting bulk data seeding for {} classes with {} records each", classNames.size(), countPerClass);
        
        for (String className : classNames) {
            try {
                Map<String, Object> classResult = seedDataForClass(className, countPerClass);
                classResults.add(classResult);
            } catch (Exception e) {
                logger.error("Error seeding data for class: {}", className, e);
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("className", className);
                errorResult.put("error", e.getMessage());
                classResults.add(errorResult);
            }
        }
        
        results.put("totalClasses", classNames.size());
        results.put("recordsPerClass", countPerClass);
        results.put("results", classResults);
        results.put("message", String.format("Bulk seeding completed for %d classes", classNames.size()));
        
        return results;
    }
    
    @Override
    public List<String> getAvailableClasses() {
        return new ArrayList<>(availableClasses);
    }
    
    @Override
    public void clearSeededData(String className) {
        seededData.remove(className);
        logger.info("Cleared seeded data for class: {}", className);
    }
    
    /**
     * Get the seeded data for a specific class
     * @param className The name of the class
     * @return List of seeded data objects
     */
    public List<Object> getSeededData(String className) {
        return seededData.getOrDefault(className, new ArrayList<>());
    }
    
    /**
     * Get all seeded data
     * @return Map with class names as keys and seeded data as values
     */
    public Map<String, List<Object>> getAllSeededData() {
        return new HashMap<>(seededData);
    }
    
    /**
     * Clear all seeded data
     */
    public void clearAllSeededData() {
        seededData.clear();
        logger.info("Cleared all seeded data");
    }
    
    /**
     * Get statistics about seeded data
     * @return Map containing statistics
     */
    public Map<String, Object> getSeedingStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalClasses", availableClasses.size());
        stats.put("seededClasses", seededData.size());
        stats.put("availableClasses", availableClasses);
        stats.put("seededClassesList", new ArrayList<>(seededData.keySet()));
        
        int totalRecords = seededData.values().stream()
            .mapToInt(List::size)
            .sum();
        stats.put("totalRecords", totalRecords);
        
        Map<String, Integer> recordsPerClass = new HashMap<>();
        seededData.forEach((className, data) -> recordsPerClass.put(className, data.size()));
        stats.put("recordsPerClass", recordsPerClass);
        
        return stats;
    }
} 