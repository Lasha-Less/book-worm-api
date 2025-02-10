package com.lb.book_worm_api.service;

import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookPeopleRoleService {

    private final BookPeopleRoleRepo bookPeopleRoleRepo;

    public BookPeopleRoleService(BookPeopleRoleRepo bookPeopleRoleRepo) {
        this.bookPeopleRoleRepo = bookPeopleRoleRepo;
    }

    //GET all
    public List<BookPeopleRole> getAllBookPeopleRoles(){
        return bookPeopleRoleRepo.findAll();
    }

    //GET single
    public Optional<BookPeopleRole> getBookPeopleRoleById(Long id){
        return bookPeopleRoleRepo.findById(id);
    }

    //CREATE single
    public BookPeopleRole createBookPeopleRole(BookPeopleRole bookPeopleRole){
        return bookPeopleRoleRepo.save(bookPeopleRole);
    }

    //UPDATE single
    public Optional<BookPeopleRole> updateBookPeopleRole(Long id, BookPeopleRole bookPeopleRoleDetails){
        return bookPeopleRoleRepo.findById(id).map(bookPeopleRole -> {
            bookPeopleRole.setBook(bookPeopleRoleDetails.getBook());
            bookPeopleRole.setRole(bookPeopleRoleDetails.getRole());
            bookPeopleRole.setPerson(bookPeopleRoleDetails.getPerson());
            return bookPeopleRoleRepo.save(bookPeopleRole);
        });
    }

    @Transactional
    public void assignRole(Book book, Person person, Role role) {
        // Check if the role is already assigned
        boolean roleExists = bookPeopleRoleRepo.existsByBookAndPersonAndRole(book, person, role);
        if (roleExists) {
            return; // Role already assigned, no need to duplicate
        }

        // Create and save a new BookPeopleRole entry
        BookPeopleRole bookPeopleRole = new BookPeopleRole(book, person, role);
        bookPeopleRoleRepo.save(bookPeopleRole);
    }


    //DELETE single
    public void deleteBookPeopleRole(Long id){
        if (!bookPeopleRoleRepo.existsById(id)){
            throw new EntityNotFoundException("BookPeopleRole not found with ID: " + id);
        }
        bookPeopleRoleRepo.deleteById(id);
    }
}
