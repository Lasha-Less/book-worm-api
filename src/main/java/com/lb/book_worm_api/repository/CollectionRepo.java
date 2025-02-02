package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepo extends JpaRepository<Collection, Long> {
}
