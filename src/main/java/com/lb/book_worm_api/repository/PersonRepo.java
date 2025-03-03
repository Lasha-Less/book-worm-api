package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.dto.PersonProjection;
import com.lb.book_worm_api.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepo extends JpaRepository<Person, Long> {

    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    Optional<Person>findByFirstNameAndLastName(String firstName, String lastName);

    List<Person>findByLastNameIgnoreCase(String lastName);

    @Query(value = "SELECT p.id AS id, " +
            "p.first_name AS firstName, " +  // Use column names
            "p.prefix AS prefix, " +
            "p.last_name AS lastName, " +
            "GROUP_CONCAT(DISTINCT br.role ORDER BY br.role ASC SEPARATOR ', ') AS roles " +
            "FROM person p " +  // Use table name
            "LEFT JOIN book_people_role br ON p.id = br.person_id " +  // Use column names
            "GROUP BY p.id, p.first_name, p.prefix, p.last_name",
            nativeQuery = true)  // 👈 **THIS IS REQUIRED**
    List<PersonProjection> findAllProjected();



}
