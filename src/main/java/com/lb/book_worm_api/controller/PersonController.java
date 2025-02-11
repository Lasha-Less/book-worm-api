package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import com.lb.book_worm_api.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/people")
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<Person>> getAllPeople(){
        List<Person>people = personService.getAllPersons();
        if (people.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable Long id){
        Person person = personService.getPersonById(id);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Person>> getPersonPyLastName(@RequestParam String lastName){
        List<Person>people = personService.getPersonByLastname(lastName);
        if (people.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/role")
    public ResponseEntity<List<Person>> getPersonsByRole(@RequestParam Role role){
        List<Person>people = personService.getPersonsByRole(role);
        if (people.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@Valid @RequestBody Person person){
        Person savedPerson = personService.createPerson(person);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPerson);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @Valid @RequestBody Person person){
        Person updatedPerson = personService.updatePerson(id, person);
        return ResponseEntity.ok(updatedPerson);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id){
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

}
