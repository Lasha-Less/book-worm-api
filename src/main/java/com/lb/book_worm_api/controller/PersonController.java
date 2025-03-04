package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.dto.PersonDTO;
import com.lb.book_worm_api.dto.PersonUpdateDTO;
import com.lb.book_worm_api.dto.PersonWithRolesDTO;
import com.lb.book_worm_api.model.Role;
import com.lb.book_worm_api.service.PersonService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/people")
public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<PersonWithRolesDTO>> getAllPeople() {
        List<PersonWithRolesDTO> people = personService.getAllPersons();
        return ResponseEntity.ok(people); // No need for an isEmpty() check
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        logger.info("Fetching person with ID: {}", id);
        PersonDTO person = personService.getPersonById(id);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonDTO>> getPersonByLastName(@RequestParam("last-name") String lastName) {
        logger.info("Searching people with last name: {}", lastName);
        return ResponseEntity.ok(personService.getPersonByLastname(lastName));
    }

    @GetMapping("/role")
    public ResponseEntity<List<PersonDTO>> getPersonsByRole(@RequestParam Role role) {
        logger.info("Fetching people with role: {}", role);
        return ResponseEntity.ok(personService.getPersonsByRole(role));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonUpdateDTO personUpdateDTO) {
        logger.info("Updating person with ID: {}", id);
        return ResponseEntity.ok(personService.updatePerson(id, personUpdateDTO));
    }

}
