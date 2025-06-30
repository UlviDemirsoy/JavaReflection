package com.acegames.backend.config;

import com.acegames.backend.application.service.ModelSchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SchemaRegistrationConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SchemaRegistrationConfig.class);
    
    @Value("${app.schema.registration.enabled:false}")
    private boolean schemaRegistrationEnabled;
    
    @Value("${app.schema.registration.classes:}")
    private String classesToRegister;
    
    @Bean
    @Profile("!test")
    public CommandLineRunner schemaRegistrationRunner(ModelSchemaService modelSchemaService) {
        return args -> {
            logger.info("Schema registration runner initialized");
            logger.info("Schema registration enabled: {}", schemaRegistrationEnabled);
            logger.info("Classes to register: '{}'", classesToRegister);
            
            if (!schemaRegistrationEnabled) {
                logger.info("Schema registration is disabled. Set app.schema.registration.enabled=true to enable automatic schema registration.");
                return;
            }
            
            logger.info("Starting automatic schema registration...");
            
            List<String> classesToRegisterList;
            if (classesToRegister == null || classesToRegister.trim().isEmpty()) {
                // Register schemas for all available classes if none specified
                classesToRegisterList = Arrays.asList("Skin", "PurchaseProduct", "Offer", "Cascade", "Tile");
                logger.info("No specific classes specified, will register schemas for all available classes: {}", classesToRegisterList);
            } else {
                // Parse the comma-separated list of classes
                classesToRegisterList = Arrays.asList(classesToRegister.split(","));
                logger.info("Will register schemas for the following classes: {}", classesToRegisterList);
            }
            
            int successCount = 0;
            int totalCount = classesToRegisterList.size();
            
            for (String className : classesToRegisterList) {
                try {
                    logger.info("Attempting to register schema for class: {}", className);
                    modelSchemaService.generateFromClassName(className);
                    logger.info("Successfully registered schema for class: {}", className);
                    successCount++;
                } catch (Exception e) {
                    logger.error("Error registering schema for class: {}", className, e);
                }
            }
            
            logger.info("Schema registration completed: {}/{} classes successfully registered", successCount, totalCount);
        };
    }
} 