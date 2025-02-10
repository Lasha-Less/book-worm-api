package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {

    boolean existsByFirstNameAndLastName(String firstName, String lastName);
    Optional<Person>findByFirstNameAndLastName(String firstName, String lastName);

}
