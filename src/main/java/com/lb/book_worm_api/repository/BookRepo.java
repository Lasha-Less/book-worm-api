package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepo extends JpaRepository<Book, Long> {

    boolean existsByTitle(String title);
}
