package com.lb.book_worm_api.validation;

import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.repository.CollectionRepo;
import org.springframework.stereotype.Component;

@Component
public class CollectionValidator {

    private final CollectionRepo collectionRepo;

    public CollectionValidator(CollectionRepo collectionRepo) {
        this.collectionRepo = collectionRepo;
    }

    /**
     * Validates if a collection exists by ID, otherwise throws an exception.
     * @param id The collection ID.
     */
    public void validateCollectionExists(Long id) {
        if (!collectionRepo.existsById(id)) {
            throw new ResourceNotFoundException("Collection not found with ID: " + id);
        }
    }

    /**
     * Validates if a collection name is unique (case-insensitive).
     * @param name The collection name.
     */
    public void validateUniqueCollectionName(String name) {
        if (collectionRepo.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("A collection with the name '" + name + "' already exists.");
        }
    }
}
