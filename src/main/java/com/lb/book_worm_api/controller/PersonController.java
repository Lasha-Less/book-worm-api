package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.dto.PersonDTO;
import com.lb.book_worm_api.dto.PersonUpdateDTO;
import com.lb.book_worm_api.dto.PersonWithRolesDTO;
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
    public ResponseEntity<List<PersonWithRolesDTO>> getAllPeople() {
        List<PersonWithRolesDTO> people = personService.getAllPersons();
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id){
        PersonDTO person = personService.getPersonById(id);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonDTO>> getPersonByLastName(@RequestParam String lastName) {
        List<PersonDTO> people = personService.getPersonByLastname(lastName);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @GetMapping("/role")
    public ResponseEntity<List<PersonDTO>> getPersonsByRole(@RequestParam Role role) {
        List<PersonDTO> people = personService.getPersonsByRole(role);
        if (people.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(people);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonUpdateDTO personUpdateDTO){
        PersonDTO updatedPerson = personService.updatePerson(id, personUpdateDTO);
        return ResponseEntity.ok(updatedPerson);
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletePerson(@PathVariable Long id){
//        personService.deletePerson(id);
//        return ResponseEntity.noContent().build();
//    }

}
