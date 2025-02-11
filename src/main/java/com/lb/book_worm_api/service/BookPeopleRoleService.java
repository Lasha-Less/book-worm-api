package com.lb.book_worm_api.service;

import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
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
        return bookPeopleRoleRepo.findById(id).map(existingRole -> {
            if (bookPeopleRoleDetails.getBook() != null) {
                existingRole.setBook(bookPeopleRoleDetails.getBook());
            }
            if (bookPeopleRoleDetails.getPerson() != null) {
                existingRole.setPerson(bookPeopleRoleDetails.getPerson());
            }
            if (bookPeopleRoleDetails.getRole() != null) {
                existingRole.setRole(bookPeopleRoleDetails.getRole());
            }
            return bookPeopleRoleRepo.save(existingRole);
        });
    }

    @Transactional
    public void assignRole(Book book, Person person, Role role) {
        bookPeopleRoleRepo.findByBookAndPersonAndRole(
                book, person, role).orElseGet(()-> bookPeopleRoleRepo.save(new BookPeopleRole(book, person, role)));
    }


    //DELETE single
    public void deleteBookPeopleRole(Long id){
        if (!bookPeopleRoleRepo.existsById(id)){
            throw new ResourceNotFoundException("BookPeopleRole not found with ID: " + id);
        }
        bookPeopleRoleRepo.deleteById(id);
    }
}
