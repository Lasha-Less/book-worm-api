package com.lb.book_worm_api.service;

import com.lb.book_worm_api.dto.*;
import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
import com.lb.book_worm_api.repository.PersonRepo;
import com.lb.book_worm_api.validation.PersonValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonService {

    Logger logger = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepo personRepo;
    private final BookPeopleRoleRepo bookPeopleRoleRepo;
    private final PersonValidator personValidator;

    public PersonService(
            PersonRepo personRepo,
            BookPeopleRoleRepo bookPeopleRoleRepo, PersonValidator personValidator) {
        this.personRepo = personRepo;
        this.bookPeopleRoleRepo = bookPeopleRoleRepo;
        this.personValidator = personValidator;
    }

    /**
     * CREATE
     */
    public List<PersonDTO> assignPeopleToBook(Book book, List<PersonRoleInputDTO> peopleWithRoles) {
        if (peopleWithRoles == null || peopleWithRoles.isEmpty()) return List.of();

        Map<String, Person> personCache = new HashMap<>();
        List<PersonDTO> assignedPeople = new ArrayList<>();

        for (PersonRoleInputDTO personDTO : peopleWithRoles) {
            // ✅ Centralized validation
            personValidator.validatePersonInput(personDTO.getLastName());

            String personKey = (personDTO.getFirstName() != null ? personDTO.getFirstName() : "") +
                    "_" + personDTO.getLastName();

            // Retrieve or create the person
            Person existingOrNewPerson = personCache.computeIfAbsent(
                    personKey, k -> getOrCreatePerson(personDTO.getFirstName(), personDTO.getLastName()));

            // Convert Person to DTO
            List<BookRoleDTO> bookRoles = existingOrNewPerson.getBookPeopleRoles().stream()
                    .map(bookPeopleRole -> new BookRoleDTO(
                            bookPeopleRole.getBook().getTitle(),
                            bookPeopleRole.getRole().toString()
                    ))
                    .collect(Collectors.toList());

            assignedPeople.add(new PersonDTO(
                    existingOrNewPerson.getId(),
                    existingOrNewPerson.getFirstName(),
                    existingOrNewPerson.getPrefix(),
                    existingOrNewPerson.getLastName(),
                    bookRoles
            ));
        }
        return assignedPeople;
    }



    @Transactional
    public Person getOrCreatePerson(String firstName, String lastName) {
        personValidator.validatePersonInput(lastName);

        final String trimmedFirstName = (firstName != null) ? firstName.trim() : null;
        final String trimmedLastName = lastName.trim();

        Optional<Person> existingPerson = personRepo.findByFirstNameAndLastName(trimmedFirstName, trimmedLastName);
        if (existingPerson.isPresent()) {
            return existingPerson.get();  // ✅ Lookup only
        }

        // ✅ Create and persist separately
        Person newPerson = new Person(trimmedFirstName, trimmedLastName);
        return personRepo.save(newPerson);
    }

    /**
     * READ
     */
    //GET all
    public List<PersonWithRolesDTO> getAllPersons() {
        Logger logger = LoggerFactory.getLogger(PersonService.class);

        List<PersonProjection> people = personRepo.findAllProjected();
        logger.debug("Fetched {} person records from database", people.size());

        return people.stream()
                .map(person -> {
                    logger.debug("Processing Person ID: {}, Last Name: {}, First Name: {}, Prefix: {}",
                            person.getId(),
                            person.getLastName(),
                            person.getFirstName() != null ? person.getFirstName() : "NULL",
                            person.getPrefix() != null ? person.getPrefix() : "NULL");

                    // ✅ Convert the roles string into a list
                    List<String> roles = person.getRoles() != null ?
                            Arrays.asList(person.getRoles().split(",")) : List.of();

                    logger.debug("Person ID {} has roles: {}", person.getId(), roles);

                    return new PersonWithRolesDTO(
                            person.getId(),
                            person.getFirstName() != null ? person.getFirstName() : "",
                            person.getPrefix() != null ? person.getPrefix() : "",
                            person.getLastName(),
                            roles
                    );
                })
                .collect(Collectors.toList());
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

    /**
     * UPDATE
     */
    //UPDATE single
    public PersonDTO updatePerson(Long id, PersonUpdateDTO personDetails){
        Person person = personRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with ID: " + id));

        // ✅ Distinguish between omitting firstName (ignore) and empty string (remove)
        if (personDetails.getFirstName() != null) {
            if (personDetails.getFirstName().isBlank()) {
                person.setFirstName(null); // Explicitly remove firstName
            } else {
                person.setFirstName(personDetails.getFirstName().trim()); // Update firstName
            }
        } // Else case: firstName is null → ignore change

        if (personDetails.getPrefix() != null && !personDetails.getPrefix().isBlank()) {
            person.setPrefix(personDetails.getPrefix().trim());
        }

        // ✅ Ensure lastName is always present and updatable
        if (personDetails.getLastName() != null && !personDetails.getLastName().isBlank()) {
            person.setLastName(personDetails.getLastName().trim());
        } else if (person.getLastName() == null || person.getLastName().isBlank()) {
            throw new ValidationException("Last name cannot be removed.");
        }

        Person updatedPerson = personRepo.save(person);

        return new PersonDTO(
                updatedPerson.getId(),
                updatedPerson.getFirstName(), // Can now be null
                updatedPerson.getPrefix(),
                updatedPerson.getLastName(),
                updatedPerson.getBookPeopleRoles().stream()
                        .map(role -> new BookRoleDTO(
                                role.getBook().getTitle(), role.getRole().toString())).collect(Collectors.toList()));
    }

    /**
     * DELETE
     */
    @Transactional
    public void removeOrphanedPerson(Long personId) {
        boolean hasRemainingRoles = bookPeopleRoleRepo.existsByPersonId(personId);

        if (!hasRemainingRoles) {
            personRepo.deleteById(personId);
        }
    }

}
