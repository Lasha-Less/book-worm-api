package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollectionRepo extends JpaRepository<Collection, Long> {
    Optional<Collection> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
}
