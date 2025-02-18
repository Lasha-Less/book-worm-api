package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookPeopleRoleRepo extends JpaRepository<BookPeopleRole, Long> {

    boolean existsByBookAndPersonAndRole(Book book, Person person, Role role);

    Optional<BookPeopleRole> findByBookAndPersonAndRole(Book book, Person person, Role role);

    @Query("SELECT DISTINCT bpr.person FROM BookPeopleRole bpr WHERE bpr.role = :role")
    List<Person>findPersonByRole(@Param("role") Role role);

    @Modifying
    @Query("DELETE FROM BookPeopleRole bpr WHERE bpr.book.id = :bookId")
    void deleteRolesByBookId(@Param("bookId") Long bookId);

}
