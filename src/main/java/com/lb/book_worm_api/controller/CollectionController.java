package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.service.CollectionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/collections")
public class CollectionController {

    private static final Logger logger = LoggerFactory.getLogger(CollectionController.class);
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping
    public ResponseEntity<List<Collection>> getAllCollections() {
        logger.info("Fetching all collections");
        return ResponseEntity.ok(collectionService.getAllCollections()); // No need for .isEmpty() check
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collection> getCollectionById(@PathVariable Long id) {
        logger.info("Fetching collection with ID: {}", id);
        Collection collection = collectionService.getCollectionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found with ID: " + id));
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> doesCollectionExist(@RequestParam String name) {
        logger.info("Checking if collection exists with name: {}", name);
        return ResponseEntity.ok(collectionService.collectionExists(name));
    }

    @PostMapping
    public ResponseEntity<Collection> createCollection(@RequestBody Collection collection) {
        logger.info("Creating a new collection: {}", collection.getName());
        Collection savedCollection = collectionService.createCollection(collection);
        return ResponseEntity.status(201).body(savedCollection);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(
            @PathVariable Long id,
            @Valid @RequestBody Collection collectionDetails) {
        logger.info("Updating collection with ID: {}", id);
        Collection updatedCollection = collectionService.updateCollection(id, collectionDetails)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found with ID: " + id));
        return ResponseEntity.ok(updatedCollection);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        logger.info("Deleting collection with ID: {}", id);
        collectionService.deleteCollection(id); // Assume deleteCollection now throws ResourceNotFoundException if ID
        // is invalid
        return ResponseEntity.noContent().build();
    }

}
