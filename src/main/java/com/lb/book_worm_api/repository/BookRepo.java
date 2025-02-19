package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepo extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    boolean existsByTitle(String title);

    List<Book>findByTitleContainingIgnoreCase(String title);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.collections WHERE b.id = :id")
    Optional<Book> findByIdWithCollections(@Param("id") Long id);

    List<Book> findByCollectionsId(Long collectionId);

    @Query("SELECT b FROM Book b JOIN b.collections c WHERE LOWER(c.name) " +
            "LIKE LOWER(CONCAT('%', :collectionName, '%'))")
    List<Book> findByCollectionNameLike(@Param("collectionName") String collectionName);

}
