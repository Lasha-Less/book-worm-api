package com.lb.book_worm_api.service;

import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.exception.DuplicateResourceException;
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

    //CREATE single
    public Person createPerson(Person person){
        if (personRepo.existsByFirstNameAndLastName(person.getFirstName(), person.getLastName())){
            throw new DuplicateResourceException(
                    "A person with the name " + person.getFirstName() + " " + person.getLastName() + " already exists");
        }
        return personRepo.save(person);
    }

    @Transactional
    public Person getOrCreatePerson(String firstname, String lastName){
        return findByName(firstname, lastName).orElseGet(()-> {
            Person newPerson = new Person(firstname, lastName);
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
        // Assign authors
        if (bookInputDTO.getAuthors() != null) {
            for (Person author : bookInputDTO.getAuthors()) {
                Person existingOrNewAuthor = getOrCreatePerson(author.getFirstName(), author.getLastName());
                bookPeopleRoleService.assignRole(book, existingOrNewAuthor, Role.AUTHOR);
            }
        }

        // Assign editors
        if (bookInputDTO.getEditors() != null) {
            for (Person editor : bookInputDTO.getEditors()) {
                Person existingOrNewEditor = getOrCreatePerson(editor.getFirstName(), editor.getLastName());
                bookPeopleRoleService.assignRole(book, existingOrNewEditor, Role.EDITOR);
            }
        }

        // Assign translators
        if (bookInputDTO.getTranslators() != null) {
            for (Person translator : bookInputDTO.getTranslators()) {
                Person existingOrNewTranslator = getOrCreatePerson(translator.getFirstName(), translator.getLastName());
                bookPeopleRoleService.assignRole(book, existingOrNewTranslator, Role.TRANSLATOR);
            }
        }

        // Assign contributors
        if (bookInputDTO.getContributors() != null) {
            for (Person contributor : bookInputDTO.getContributors()) {
                Person existingOrNewContributor = getOrCreatePerson(contributor.getFirstName(), contributor.getLastName());
                bookPeopleRoleService.assignRole(book, existingOrNewContributor, Role.CONTRIBUTOR);
            }
        }

        // Assign illustrators
        if (bookInputDTO.getIllustrators() != null) {
            for (Person illustrator : bookInputDTO.getIllustrators()) {
                Person existingOrNewIllustrator = getOrCreatePerson(illustrator.getFirstName(), illustrator.getLastName());
                bookPeopleRoleService.assignRole(book, existingOrNewIllustrator, Role.ILLUSTRATOR);
            }
        }

        // Assign others
        if (bookInputDTO.getOthers() != null) {
            for (Person other : bookInputDTO.getOthers()) {
                Person existingOrNewOther = getOrCreatePerson(other.getFirstName(), other.getLastName());
                bookPeopleRoleService.assignRole(book, existingOrNewOther, Role.OTHER);
            }
        }
    }


    //DELETE single
    public void deletePerson(Long id){
        Person person = personRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Person not found with ID:" + id));
        personRepo.delete(person);
    }

}
