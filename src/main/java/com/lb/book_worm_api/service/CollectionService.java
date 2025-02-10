package com.lb.book_worm_api.service;

import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.repository.CollectionRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CollectionService {

    private final CollectionRepo collectionRepo;

    public CollectionService(CollectionRepo collectionRepo) {
        this.collectionRepo = collectionRepo;
    }

    //GET all
    public List<Collection> getAllCollections() {
        return collectionRepo.findAll();
    }

    //GET single
    public Optional<Collection> getCollectionById(Long id){
        return collectionRepo.findById(id);
    }

    public Optional<Collection> findByName(String name) {
        return collectionRepo.findByName(name);
    }

    //CREATE single
    public Collection createCollection(Collection collection){
        return collectionRepo.save(collection);
    }

    //UPDATE single
    public Optional<Collection> updateCollection(Long id, Collection collectionDetails){
        return collectionRepo.findById(id).map(collection -> {
            collection.setName(collectionDetails.getName());
            return collectionRepo.save(collection);
        });
    }

    //DELETE single
    public void deleteCollection(Long id){
        if (!collectionRepo.existsById(id)){
            throw new EntityNotFoundException("Collection not found with ID: " + id);
        }
        collectionRepo.deleteById(id);
    }

}
