package com.acegames.backend.infrastructure.repository;

import com.acegames.backend.infrastructure.model.ModelSchemaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ModelSchemaRepository extends MongoRepository<ModelSchemaDocument, String> {
    Optional<ModelSchemaDocument> findByCollection(String collection);
    void deleteByCollection(String collection);

}
