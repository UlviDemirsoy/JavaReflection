package com.acegames.backend.web.controller;

import com.acegames.backend.application.dto.ModelSchemaDto;
import com.acegames.backend.application.dto.FieldDefinition;
import com.acegames.backend.application.helper.EnumScanner;
import com.acegames.backend.application.helper.ReflectionSchemaParser;
import com.acegames.backend.application.service.ModelSchemaService;
import com.acegames.backend.web.exception.ApiErrorResponse;
import io.opentelemetry.api.logs.Logger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/schema")
@Tag(name = "Schema API", description = "Manage dynamic model schemas")
public class SchemaController {

    private final ModelSchemaService schemaService;
    private final Logger logger;

    public SchemaController(ModelSchemaService schemaService, Logger logger) {
        this.schemaService = schemaService;
        this.logger = logger;
    }



    @Operation(summary = "Get schema by collection name")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Schema found",
                    content = @Content(schema = @Schema(implementation = ModelSchemaDto.class))),
            @ApiResponse(responseCode = "404", description = "Schema not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{collection}")
    public ResponseEntity<ModelSchemaDto> getSchema(@PathVariable String collection) {
        logger.logRecordBuilder()
                .setBody("Fetching schema for collection: " + collection)
                .emit();

        return schemaService.getSchema(collection)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.logRecordBuilder()
                            .setBody("Schema not found for collection: " + collection)
                            .emit();
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Get all registered schemas")
    @ApiResponse(responseCode = "200", description = "List of all schemas",
            content = @Content(schema = @Schema(implementation = ModelSchemaDto.class)))
    @GetMapping
    public ResponseEntity<List<ModelSchemaDto>> getAllSchemas() {
        logger.logRecordBuilder()
                .setBody("Fetching all registered schemas")
                .emit();

        return ResponseEntity.ok(schemaService.getAllSchemas());
    }

    @PostMapping("/register")
    @Operation(summary = "Register schema from class name")
    public ResponseEntity<Void> registerSchemaFromClass(@RequestParam String className) {
        schemaService.generateFromClassName(className);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/register/bulk")
    @Operation(summary = "Register schemas for multiple classes")
    public ResponseEntity<Map<String, Object>> registerSchemasFromClasses(@RequestBody List<String> classNames) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> successList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        
        for (String className : classNames) {
            try {
                schemaService.generateFromClassName(className);
                successList.add(className);
            } catch (Exception e) {
                errorList.add(className + ": " + e.getMessage());
            }
        }
        
        result.put("totalClasses", classNames.size());
        result.put("successful", successList);
        result.put("errors", errorList);
        result.put("message", String.format("Processed %d classes: %d successful, %d errors", 
            classNames.size(), successList.size(), errorList.size()));
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register/default")
    @Operation(summary = "Register schemas for default classes (Skin, PurchaseProduct, Offer, Cascade)")
    public ResponseEntity<Map<String, Object>> registerDefaultSchemas() {
        List<String> defaultClasses = List.of("Skin", "PurchaseProduct", "Offer", "Cascade");
        return registerSchemasFromClasses(defaultClasses);
    }


    @DeleteMapping("/{collection}")
    @Operation(summary = "Delete schema by collection name")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Schema deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Schema not found")
    })
    public ResponseEntity<Void> deleteSchema(@PathVariable String collection) {
        schemaService.deleteByCollectionName(collection);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


    @GetMapping("/enums")
    @Operation(summary = "List all available enums and their values")
    public ResponseEntity<Map<String, String[]>> getAllEnums() {
        Map<String, String[]> enums = EnumScanner.scanEnums();
        return ResponseEntity.ok(enums);
    }

    @PostMapping("/test-reflection")
    @Operation(summary = "Test reflection parser with a specific class")
    public ResponseEntity<Map<String, Object>> testReflection(@RequestParam String className) {
        try {
            Class<?> clazz = Class.forName("com.acegames.backend.domain.model." + className);
            Map<String, Object> result = new LinkedHashMap<>();
            
            // Reflection parser'ı test et
            Map<String, FieldDefinition> fields = ReflectionSchemaParser.parseClass(clazz);
            result.put("className", className);
            result.put("fields", fields);
            
            return ResponseEntity.ok(result);
        } catch (ClassNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Class not found: " + className));
        }
    }

//    @GetMapping("/validate-references/{className}")
//    @Operation(summary = "Validate references for a specific class")
//    public ResponseEntity<Map<String, List<String>>> validateReferences(@PathVariable String className) {
//        Map<String, List<String>> validationResult = schemaService.validateReferences(className);
//
//        if (validationResult.isEmpty()) {
//            return ResponseEntity.ok(Map.of("message", "All references are valid"));
//        } else {
//            return ResponseEntity.badRequest().body(validationResult);
//        }
//    }


}
