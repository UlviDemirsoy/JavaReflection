package com.acegames.backend.infrastructure.service;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GenericCrudServiceImplTest {

    private MongoTemplate mongoTemplate;
    private GenericCrudServiceImpl service;

    @BeforeEach
    void setUp() {
        mongoTemplate = mock(MongoTemplate.class);
        service = new GenericCrudServiceImpl(mongoTemplate);
    }

    @Test
    void shouldConvertObjectIdToHexStringWhenFindAllIsCalled() {
        // Arrange
        Document doc1 = new Document("_id", new ObjectId());
        Document doc2 = new Document("_id", new ObjectId());
        List<Document> docs = Arrays.asList(doc1, doc2);
        when(mongoTemplate.findAll(Document.class, "testCollection")).thenReturn(docs);

        // Act
        List<Document> result = service.findAll("testCollection");

        // Assert
        assertEquals(2, result.size());
        for (Document doc : result) {
            assertTrue(doc.get("_id") instanceof String);
            assertEquals(24, ((String) doc.get("_id")).length());
        }
        verify(mongoTemplate, times(1)).findAll(Document.class, "testCollection");
    }

    @Test
    void shouldThrowExceptionWhenFindByIdWithInvalidId() {
        assertThrows(IllegalArgumentException.class, () ->
            service.findById("testCollection", "invalid_id")
        );
    }

    @Test
    void shouldReturnNullWhenFindByIdNotFound() {
        String id = new ObjectId().toHexString();
        when(mongoTemplate.findById(any(ObjectId.class), eq(Document.class), eq("testCollection"))).thenReturn(null);
        assertNull(service.findById("testCollection", id));
    }

    @Test
    void shouldConvertObjectIdToHexStringWhenFindByIdIsCalled() {
        ObjectId objectId = new ObjectId();
        Document doc = new Document("_id", objectId);
        when(mongoTemplate.findById(eq(objectId), eq(Document.class), eq("testCollection"))).thenReturn(doc);
        Document result = service.findById("testCollection", objectId.toHexString());
        assertNotNull(result);
        assertTrue(result.get("_id") instanceof String);
        assertEquals(objectId.toHexString(), result.get("_id"));
    }

    @Test
    void shouldInsertDocumentAndReturnWithHexStringId() {
        Map<String, Object> data = new HashMap<>();
        data.put("field", "value");
        ObjectId objectId = new ObjectId();
        Document inserted = new Document(data);
        inserted.put("_id", objectId);
        when(mongoTemplate.insert(any(Document.class), eq("testCollection"))).thenReturn(inserted);
        Document result = service.insert("testCollection", data);
        assertNotNull(result);
        assertTrue(result.get("_id") instanceof String);
        assertEquals(objectId.toHexString(), result.get("_id"));
    }

    @Test
    void shouldThrowExceptionWhenUpdateWithInvalidId() {
        assertThrows(IllegalArgumentException.class, () ->
            service.update("testCollection", "invalid_id", new HashMap<>())
        );
    }

    @Test
    void shouldUpdateDocumentAndReturnWithHexStringId() {
        ObjectId objectId = new ObjectId();
        Map<String, Object> data = new HashMap<>();
        data.put("field", "newValue");
        data.put("_id", "shouldBeRemoved");
        Document updated = new Document(data);
        updated.put("_id", objectId);
        when(mongoTemplate.findById(eq(objectId), eq(Document.class), eq("testCollection"))).thenReturn(updated);
        @SuppressWarnings("unchecked")
        MongoCollection<org.bson.Document> mockCollection = mock(MongoCollection.class);
        when(mongoTemplate.getCollection(anyString())).thenReturn(mockCollection);
        Document result = service.update("testCollection", objectId.toHexString(), new HashMap<>(data));
        assertNotNull(result);
        assertTrue(result.get("_id") instanceof String);
        assertEquals(objectId.toHexString(), result.get("_id"));
    }

    @Test
    void shouldThrowExceptionWhenDeleteWithInvalidId() {
        assertThrows(IllegalArgumentException.class, () ->
            service.delete("testCollection", "invalid_id")
        );
    }

    @Test
    void shouldCallRemoveWhenDeleteWithValidId() {
        ObjectId objectId = new ObjectId();
        service.delete("testCollection", objectId.toHexString());
        verify(mongoTemplate, times(1)).remove(any(Query.class), eq("testCollection"));
    }
} 