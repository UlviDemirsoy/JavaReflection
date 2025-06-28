package com.acegames.backend.web.controller;

import com.acegames.backend.application.service.GenericCrudService;
import com.acegames.backend.web.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content")
@Tag(name = "Generic Content API", description = "Dynamic CRUD operations for any collection")
public class GenericCrudController {

    private final GenericCrudService crudService;

    public GenericCrudController(GenericCrudService crudService) {
        this.crudService = crudService;
    }

    @Operation(summary = "Get all documents in a collection")
    @ApiResponse(responseCode = "200", description = "List of documents retrieved successfully")
    @GetMapping("/{collection}")
    public ResponseEntity<List<Document>> getAll(@PathVariable String collection) {
        return ResponseEntity.ok(crudService.findAll(collection));
    }

    @Operation(summary = "Get a document by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document found"),
            @ApiResponse(responseCode = "404", description = "Document not found", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{collection}/{id}")
    public ResponseEntity<Document> getById(@PathVariable String collection, @PathVariable String id) {
        Document doc = crudService.findById(collection, id);
        return doc != null ? ResponseEntity.ok(doc) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Create a new document in the collection")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Document created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data format", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping("/{collection}")
    public ResponseEntity<Document> create(
            @PathVariable String collection,
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> data
    ) {
        Document doc = crudService.insert(collection, data);
        return ResponseEntity.status(201).body(doc); // Daha doğru status
    }

    @Operation(summary = "Update an existing document by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Document updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid update request", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PutMapping("/{collection}/{id}")
    public ResponseEntity<Document> update(
            @PathVariable String collection,
            @PathVariable String id,
            @org.springframework.web.bind.annotation.RequestBody Map<String, Object> data
    ) {
        Document updated = crudService.update(collection, id, data);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a document by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Document deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid delete request", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @DeleteMapping("/{collection}/{id}")
    public ResponseEntity<Void> delete(@PathVariable String collection, @PathVariable String id) {
        crudService.delete(collection, id);
        return ResponseEntity.noContent().build();
    }
}
