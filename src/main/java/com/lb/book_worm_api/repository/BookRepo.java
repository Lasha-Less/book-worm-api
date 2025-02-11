package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

    boolean existsByTitle(String title);
    List<Book>findByTitleContainingIgnoreCase(String title);
}
