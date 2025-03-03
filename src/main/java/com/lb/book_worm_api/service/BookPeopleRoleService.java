package com.lb.book_worm_api.service;

import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.dto.BookUpdateDTO;
import com.lb.book_worm_api.dto.PersonDTO;
import com.lb.book_worm_api.dto.PersonRoleInputDTO;
import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
import com.lb.book_worm_api.repository.PersonRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookPeopleRoleService {

    private final PersonService personService;
    private final BookPeopleRoleRepo bookPeopleRoleRepo;
    private final PersonRepo personRepo;

    public BookPeopleRoleService(PersonService personService,
                                 BookPeopleRoleRepo bookPeopleRoleRepo,
                                 PersonRepo personRepo) {
        this.personService = personService;
        this.bookPeopleRoleRepo = bookPeopleRoleRepo;
        this.personRepo = personRepo;
    }

    public void assignPeopleToBook(Book book, BookInputDTO bookInputDTO) {
        assignPeopleToBook(book, bookInputDTO.getAuthors(), Role.AUTHOR);
        assignPeopleToBook(book, bookInputDTO.getEditors(), Role.EDITOR);
        assignOthersWithRoles(book, bookInputDTO.getOthers());
    }

    public void updateBookPeople(Book book, BookUpdateDTO updateDTO) {
        if (updateDTO.getAuthors() != null) {
            assignPeopleToBook(book, updateDTO.getAuthors(), Role.AUTHOR);
        }
        if (updateDTO.getEditors() != null) {
            assignPeopleToBook(book, updateDTO.getEditors(), Role.EDITOR);
        }
        if (updateDTO.getOthers() != null) {
            assignOthersWithRoles(book, updateDTO.getOthers());
        }
    }

    private void assignOthersWithRoles(Book book, List<PersonRoleInputDTO> others) {
        if (others != null) {
            for (PersonRoleInputDTO personRole : others) {
                Role role;
                try {
                    role = (personRole.getRole() != null) ?
                            Role.valueOf(personRole.getRole().toUpperCase()) : Role.OTHER;
                } catch (IllegalArgumentException e) {
                    role = Role.OTHER; // Default to OTHER if the role string is invalid
                }
                assignPeopleToBook(book, List.of(personRole), role);
            }
        }
    }

    //Test implementation REMOVE if it does not work
    @Transactional
    public void assignRole(Book book, Person person, Role role) {
        bookPeopleRoleRepo.findByBookAndPersonAndRole(
                book, person, role).orElseGet(()-> bookPeopleRoleRepo.save(new BookPeopleRole(book, person, role)));
    }

    //Test implementation REMOVE if it does not work
    @Transactional
    public void assignPeopleToBook(Book book, List<PersonRoleInputDTO> peopleWithRoles, Role role) {
        List<PersonDTO> peopleDTOs = personService.assignPeopleToBook(book, peopleWithRoles);

        for (PersonDTO personDTO : peopleDTOs) {
            // Retrieve the actual Person entity from the database before assigning a role
            Person person = personRepo.findById(personDTO.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + personDTO.getId()));

            assignRole(book, person, role);
        }
    }


}
