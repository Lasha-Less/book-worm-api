package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.service.CollectionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/collections")
public class CollectionController {

    CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping()
    public ResponseEntity<List<Collection>> getAllCollections(){
        List<Collection> collections = collectionService.getAllCollections();
        if (collections.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(collections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collection> getCollectionById(@PathVariable Long id){
        Optional<Collection>collection = collectionService.getCollectionById(id);
        return collection.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> doesCollectionExist(@RequestParam String name){
        boolean exists = collectionService.collectionExists(name);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<Collection> createCollection(@RequestBody Collection collection){
        Collection savedCollection = collectionService.createCollection(collection);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCollection);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(@PathVariable Long id, @Valid @RequestBody Collection collectionDetails){
        Optional<Collection> updatedCollection = collectionService.updateCollection(id, collectionDetails);
        return updatedCollection.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Collection> deleteCollection(@PathVariable Long id){
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }

}
