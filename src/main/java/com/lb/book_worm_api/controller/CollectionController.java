package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.service.CollectionService;
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
    public List<Collection> getAllCollections(){
        return collectionService.getAllCollections();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Collection> getCollectionById(@PathVariable Long id){
        Optional<Collection>collection = collectionService.getCollectionById(id);
        return collection.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Collection> createCollection(@RequestBody Collection collection){
        Collection savedCollection = collectionService.createCollection(collection);
        return ResponseEntity.ok(savedCollection);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(@PathVariable Long id, @RequestBody Collection collectionDetails){
        Optional<Collection> updatedCollection = collectionService.updateCollection(id, collectionDetails);
        return updatedCollection.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Collection> deleteCollection(@PathVariable Long id){
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }

}
