package com.acegames.backend.web.controller;

import com.acegames.backend.infrastructure.model.ModelSchemaDocument;
import com.acegames.backend.infrastructure.repository.ModelSchemaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SchemaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ModelSchemaRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        ModelSchemaDocument doc = ModelSchemaDocument.builder()
                .collection("testcollection")
                .displayName("Test Collection")
                .fields(Collections.emptyMap())
                .build();
        repository.save(doc);
    }

    @Test
    void shouldReturnAllRegisteredSchemas() throws Exception {
        mockMvc.perform(get("/api/schema")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].collection").value("testcollection"))
            .andExpect(jsonPath("$[0].displayName").value("Test Collection"));
    }

    @Test
    void shouldReturnSchemaByCollectionName() throws Exception {
        mockMvc.perform(get("/api/schema/testcollection"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.collection").value("testcollection"));
    }

    @Test
    void shouldReturn404ForNonexistentSchema() throws Exception {
        mockMvc.perform(get("/api/schema/doesnotexist"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateSchemaWithRegisterEndpoint() throws Exception {
        mockMvc.perform(post("/api/schema/register")
                .param("className", "Skin"))
            .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnErrorForInvalidClassNameOnRegister() throws Exception {
        mockMvc.perform(post("/api/schema/register")
                .param("className", "NonExistentClass"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteSchemaByCollectionName() throws Exception {
        mockMvc.perform(delete("/api/schema/testcollection"))
            .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/schema/testcollection"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnEnums() throws Exception {
        mockMvc.perform(get("/api/schema/enums"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
} 