package com.lb.book_worm_api.service;

import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.repository.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepo personRepo;

    public PersonService(PersonRepo personRepo) {
        this.personRepo = personRepo;
    }

    public List<Person> getAllPersons(){
        return personRepo.findAll();
    }

    public Optional<Person> getPersonById(Long id){
        return personRepo.findById(id);
    }

    public Person createPerson(Person person){
        return personRepo.save(person);
    }

    public Optional<Person> updatePerson(Long id, Person personDetails){
        return personRepo.findById(id).map(person -> {
            person.setFirstName(personDetails.getFirstName());
            person.setPrefix(personDetails.getPrefix());
            person.setLastName(personDetails.getLastName());
            return personRepo.save(person);
        });
    }

    public void deletePerson(Long id){
        personRepo.deleteById(id);
    }

}
