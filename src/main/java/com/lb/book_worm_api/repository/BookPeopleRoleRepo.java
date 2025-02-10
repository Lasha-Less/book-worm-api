package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookPeopleRoleRepo extends JpaRepository<BookPeopleRole, Long> {

    boolean existsByBookAndPersonAndRole(Book book, Person person, Role role);
}
