package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.BookPeopleRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookPeopleRoleRepo extends JpaRepository<BookPeopleRole, Long> {
}
