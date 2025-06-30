package com.acegames.backend.infrastructure.service;

import com.acegames.backend.application.service.GenericCrudService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GenericCrudServiceImpl implements GenericCrudService {

    private final MongoTemplate mongoTemplate;

    public GenericCrudServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Document> findAll(String collectionName) {
        List<Document> docs = mongoTemplate.findAll(Document.class, collectionName);

        for (Document doc : docs) {
            Object idObj = doc.get("_id");
            if (idObj instanceof ObjectId) {
                doc.put("_id", ((ObjectId) idObj).toHexString());
            }
        }

        return docs;
    }

    @Override
    public Document findById(String collectionName, String id) {
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ObjectId: " + id);
        }
        Document doc = mongoTemplate.findById(new ObjectId(id), Document.class, collectionName);
        if (doc != null && doc.get("_id") instanceof ObjectId) {
            doc.put("_id", ((ObjectId) doc.get("_id")).toHexString());
        }
        return doc;
    }

    @Override
    public Document insert(String collectionName, Map<String, Object> data) {
        Document doc = new Document(data);
        Document result = mongoTemplate.insert(doc, collectionName);
        Object idObj = result.get("_id");
        if (idObj instanceof ObjectId) {
            result.put("_id", ((ObjectId) idObj).toHexString());
        }
        return result;
    }

    @Override
    public Document update(String collectionName, String id, Map<String, Object> data) {
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ObjectId: " + id);
        }

        ObjectId objectId = new ObjectId(id);

        data.remove("_id");

        Query query = new Query(Criteria.where("_id").is(objectId));
        Document updateDoc = new Document();
        updateDoc.putAll(data);

        Document setDoc = new Document("$set", updateDoc);
        mongoTemplate.getCollection(collectionName).updateOne(query.getQueryObject(), setDoc);

        Document updated = mongoTemplate.findById(objectId, Document.class, collectionName);
        if (updated != null && updated.get("_id") instanceof ObjectId) {
            updated.put("_id", ((ObjectId) updated.get("_id")).toHexString());
        }
        return updated;
    }

    @Override
    public void delete(String collectionName, String id) {
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ObjectId: " + id);
        }

        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        mongoTemplate.remove(query, collectionName);
    }
}
