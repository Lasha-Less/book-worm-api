package com.lb.book_worm_api.service;

import com.lb.book_worm_api.dto.BookInputDTO;
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
    public List<Person> getAllPersons(){
        return personRepo.findAll();
    }

    //GET single
    public Person getPersonById(Long id){
        return personRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Person not found with ID: " + id));
    }

    public Optional<Person> findByName(String firstName, String lastName) {
        return personRepo.findByFirstNameAndLastName(firstName, lastName);
    }

    public List<Person> getPersonByLastname(String lastName){
        return personRepo.findByLastNameIgnoreCase(lastName);
    }

    public List<Person> getPersonsByRole(Role role){
        return bookPeopleRoleRepo.findPersonByRole(role);
    }

    //CREATE single
    public Person createPerson(Person person){
        if (personRepo.existsByFirstNameAndLastName(person.getFirstName(), person.getLastName())){
            throw new DuplicateResourceException(
                    "A person with the name " + person.getFirstName() + " " + person.getLastName() + " already exists");
        }
        return personRepo.save(person);
    }

    @Transactional
    public Person getOrCreatePerson(String firstName, String lastName){
        if (firstName == null || firstName.isBlank() || lastName == null || lastName.isBlank()) {
            throw new ValidationException("Both first name and last name are required.");
        }
        return findByName(firstName, lastName).orElseGet(()-> {
            Person newPerson = new Person(firstName, lastName);
            return personRepo.save(newPerson);
        });
    }

    //UPDATE single
    public Person updatePerson(Long id, Person personDetails){
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
        return personRepo.save(person);
    }

    @Transactional
    public void assignPeopleToBook(Book book, BookInputDTO bookInputDTO) {

        Map<String, Person> personCache = new HashMap<>();

        assignRoleToPeople(book, bookInputDTO.getAuthors(), Role.AUTHOR, personCache);
        assignRoleToPeople(book, bookInputDTO.getEditors(), Role.EDITOR, personCache);
        assignRoleToPeople(book, bookInputDTO.getTranslators(), Role.TRANSLATOR, personCache);
        assignRoleToPeople(book, bookInputDTO.getContributors(), Role.CONTRIBUTOR, personCache);
        assignRoleToPeople(book, bookInputDTO.getIllustrators(), Role.ILLUSTRATOR, personCache);
        assignRoleToPeople(book, bookInputDTO.getOthers(), Role.OTHER, personCache);
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
    public void deletePerson(Long id){
        Person person = personRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(
                        "Cannot delete: Person with ID " + id + " does not exist."));
        personRepo.delete(person);
    }

}
