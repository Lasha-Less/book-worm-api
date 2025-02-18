package com.lb.book_worm_api.service;

import com.lb.book_worm_api.dto.*;
import com.lb.book_worm_api.exception.DuplicateResourceException;
import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
import com.lb.book_worm_api.repository.PersonRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepo personRepo;
    private final BookPeopleRoleService bookPeopleRoleService;
    private final BookPeopleRoleRepo bookPeopleRoleRepo;

    public PersonService(
            PersonRepo personRepo,
            BookPeopleRoleService bookPeopleRoleService,
            BookPeopleRoleRepo bookPeopleRoleRepo) {
        this.personRepo = personRepo;
        this.bookPeopleRoleService = bookPeopleRoleService;
        this.bookPeopleRoleRepo = bookPeopleRoleRepo;
    }

    //GET all
    public List<PersonDTO> getAllPersons() {
        List<Person> people = personRepo.findAll();

        return people.stream()
                .map(person -> new PersonDTO(
                        person.getId(),
                        person.getFirstName(),
                        person.getPrefix(),
                        person.getLastName(),
                        person.getBookPeopleRoles().stream()
                                .map(role -> new BookRoleDTO(role.getBook().getTitle(), role.getRole()
                                        .toString())).collect(Collectors.toList()))).collect(Collectors.toList());
    }

    //GET single
    public PersonDTO getPersonById(Long id) {
        Person person = personRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + id));

        List<BookRoleDTO> books = person.getBookPeopleRoles().stream()
                .map(bookPeopleRole -> new BookRoleDTO(
                        bookPeopleRole.getBook().getTitle(), // Extracts the book title
                        bookPeopleRole.getRole().toString()  // Extracts the role name
                ))
                .collect(Collectors.toList());

        return new PersonDTO(person.getId(), person.getFirstName(), person.getPrefix(), person.getLastName(), books);
    }

    public List<PersonDTO> getPersonByLastname(String lastName) {
        List<Person> people = personRepo.findByLastNameIgnoreCase(lastName);

        return people.stream()
                .map(person -> new PersonDTO(
                        person.getId(),
                        person.getFirstName(),
                        person.getPrefix(),
                        person.getLastName(),
                        person.getBookPeopleRoles().stream()
                                .map(role -> new BookRoleDTO(
                                        role.getBook().getTitle(),
                                        role.getRole().toString()))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    public List<PersonDTO> getPersonsByRole(Role role) {
        List<Person> people = bookPeopleRoleRepo.findPersonByRole(role);

        return people.stream()
                .map(person -> new PersonDTO(
                        person.getId(),
                        person.getFirstName(),
                        person.getPrefix(),
                        person.getLastName(),
                        person.getBookPeopleRoles().stream()
                                .map(roleEntry -> new BookRoleDTO(
                                        roleEntry.getBook().getTitle(),
                                        roleEntry.getRole().toString()))
                                .collect(Collectors.toList()))).collect(Collectors.toList());
    }

    @Transactional
    public Person getOrCreatePerson(String firstName, String lastName){
        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new ValidationException("Both first name and last name are required.");
        }
        return personRepo.findByFirstNameAndLastName(firstName, lastName).orElseGet(()-> {
            Person newPerson = new Person(firstName, lastName);
            return personRepo.save(newPerson);
        });
    }

    //UPDATE single
    public PersonDTO updatePerson(Long id, PersonUpdateDTO personDetails){
        Person person = personRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Person not found with ID: " + id));
        if (personDetails.getFirstName() != null && !personDetails.getFirstName().isBlank()){
            person.setFirstName(personDetails.getFirstName());
        }
        if (personDetails.getPrefix() != null && !personDetails.getPrefix().isBlank()){
            person.setPrefix(personDetails.getPrefix());
        }
        if (personDetails.getLastName() != null && !personDetails.getLastName().isBlank()){
            person.setLastName(personDetails.getLastName());
        }

        Person updatedPerson = personRepo.save(person);

        return new PersonDTO(
                updatedPerson.getId(),
                updatedPerson.getFirstName(),
                updatedPerson.getPrefix(),
                updatedPerson.getLastName(),
                updatedPerson.getBookPeopleRoles().stream()
                        .map(role -> new BookRoleDTO(role.getBook().getTitle(), role.getRole()
                                .toString())).collect(Collectors.toList())
        );
    }

    @Transactional
    public void assignPeopleToBook(Book book, List<PersonRoleInputDTO> peopleWithRoles, Role role) {
        if (peopleWithRoles == null || peopleWithRoles.isEmpty()) return;

        Map<String, Person> personCache = new HashMap<>();

        for (PersonRoleInputDTO personDTO : peopleWithRoles) {
            String personKey = personDTO.getFirstName() + "_" + personDTO.getLastName();

            // Retrieve or create the person
            Person existingOrNewPerson = personCache.computeIfAbsent(
                    personKey, k -> getOrCreatePerson(personDTO.getFirstName(), personDTO.getLastName()));

            // Assign role
            bookPeopleRoleService.assignRole(book, existingOrNewPerson, role);
        }
    }


    private void assignRoleToPeople(Book book, List<Person> people, Role role, Map<String, Person>personCache){
        if (people==null) return;

        for (Person person : people) {
            String personKey = person.getFirstName() + "_" + person.getLastName();
            Person existingOrNewPerson = personCache.computeIfAbsent(
                    personKey, k -> getOrCreatePerson(person.getFirstName(), person.getLastName()));
            bookPeopleRoleService.assignRole(book, existingOrNewPerson, role);
        }
    }

    //DELETE single
//    public void deletePerson(Long id){
//        Person person = personRepo.findById(id)
//                .orElseThrow(()-> new ResourceNotFoundException(
//                        "Cannot delete: Person with ID " + id + " does not exist."));
//        personRepo.delete(person);
//    }

}
