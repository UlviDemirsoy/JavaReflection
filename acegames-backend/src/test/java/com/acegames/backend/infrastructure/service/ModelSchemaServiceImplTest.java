package com.acegames.backend.infrastructure.service;

import com.acegames.backend.infrastructure.model.ModelSchemaDocument;
import com.acegames.backend.infrastructure.repository.ModelSchemaRepository;
import com.acegames.backend.web.exception.DuplicateSchemaException;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ModelSchemaServiceImplTest {
    private ModelSchemaRepository repository;
    private ModelSchemaServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(ModelSchemaRepository.class);
        service = new ModelSchemaServiceImpl(repository);
    }

    @Test
    void testGetAllSchemasReturnsEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(service.getAllSchemas().isEmpty());
    }

    // Add more tests for other methods as needed
} 