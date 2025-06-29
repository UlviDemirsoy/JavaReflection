package com.acegames.backend.web.controller;

import com.acegames.backend.application.service.DataSeedingService;
import com.acegames.backend.infrastructure.service.DataSeedingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seeding")
@CrossOrigin(origins = "*")
public class DataSeedingController {
    
    private final DataSeedingService dataSeedingService;
    private final DataSeedingServiceImpl dataSeedingServiceImpl;
    
    public DataSeedingController(DataSeedingService dataSeedingService, 
                                DataSeedingServiceImpl dataSeedingServiceImpl) {
        this.dataSeedingService = dataSeedingService;
        this.dataSeedingServiceImpl = dataSeedingServiceImpl;
    }
    
    /**
     * Get available classes that can be seeded
     */
    @GetMapping("/available-classes")
    public ResponseEntity<List<String>> getAvailableClasses() {
        return ResponseEntity.ok(dataSeedingService.getAvailableClasses());
    }
    
    /**
     * Seed data for a specific class
     */
    @PostMapping("/seed/{className}")
    public ResponseEntity<Map<String, Object>> seedDataForClass(
            @PathVariable String className,
            @RequestParam(defaultValue = "5") int count) {
        
        Map<String, Object> result = dataSeedingService.seedDataForClass(className, count);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Seed data for multiple classes
     */
    @PostMapping("/seed/bulk")
    public ResponseEntity<Map<String, Object>> seedDataForClasses(
            @RequestBody List<String> classNames,
            @RequestParam(defaultValue = "3") int countPerClass) {
        
        Map<String, Object> result = dataSeedingService.seedDataForClasses(classNames, countPerClass);
        return ResponseEntity.ok(result);
    }
    
    /**
     * Get seeded data for a specific class
     */
    @GetMapping("/data/{className}")
    public ResponseEntity<List<Object>> getSeededData(@PathVariable String className) {
        List<Object> data = dataSeedingServiceImpl.getSeededData(className);
        return ResponseEntity.ok(data);
    }
    
    /**
     * Get all seeded data
     */
    @GetMapping("/data")
    public ResponseEntity<Map<String, List<Object>>> getAllSeededData() {
        Map<String, List<Object>> data = dataSeedingServiceImpl.getAllSeededData();
        return ResponseEntity.ok(data);
    }
    
    /**
     * Get seeding statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getSeedingStatistics() {
        Map<String, Object> stats = dataSeedingServiceImpl.getSeedingStatistics();
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Clear seeded data for a specific class
     */
    @DeleteMapping("/data/{className}")
    public ResponseEntity<Map<String, String>> clearSeededData(@PathVariable String className) {
        dataSeedingService.clearSeededData(className);
        return ResponseEntity.ok(Map.of("message", "Seeded data cleared for class: " + className));
    }
    
    /**
     * Clear all seeded data
     */
    @DeleteMapping("/data")
    public ResponseEntity<Map<String, String>> clearAllSeededData() {
        dataSeedingServiceImpl.clearAllSeededData();
        return ResponseEntity.ok(Map.of("message", "All seeded data cleared"));
    }
    
    /**
     * Seed data for all available classes
     */
    @PostMapping("/seed/all")
    public ResponseEntity<Map<String, Object>> seedAllClasses(
            @RequestParam(defaultValue = "3") int countPerClass) {
        
        List<String> allClasses = dataSeedingService.getAvailableClasses();
        Map<String, Object> result = dataSeedingService.seedDataForClasses(allClasses, countPerClass);
        return ResponseEntity.ok(result);
    }
} 