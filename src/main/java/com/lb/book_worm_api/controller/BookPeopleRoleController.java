package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.service.BookPeopleRoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/book-people-roles")
public class BookPeopleRoleController {

    private final BookPeopleRoleService bookPeopleRoleService;

    public BookPeopleRoleController(BookPeopleRoleService bookPeopleRoleService) {
        this.bookPeopleRoleService = bookPeopleRoleService;
    }

    @PostMapping //CREATE BookPeopleRole
    public ResponseEntity<BookPeopleRole> createBookPeopleRole(@Valid @RequestBody BookPeopleRole bookPeopleRole){
        BookPeopleRole savedRole = bookPeopleRoleService.createBookPeopleRole(bookPeopleRole);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
    }

    @GetMapping //READ all BookPeopleRoles
    public ResponseEntity<List<BookPeopleRole>> getAllBookPeopleRoles(){
        List<BookPeopleRole> roles = bookPeopleRoleService.getAllBookPeopleRoles();
        if (roles.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}") //READ BookPeopleRole
    public ResponseEntity<BookPeopleRole> getBookPeopleRoleById(@PathVariable Long id){
        return bookPeopleRoleService.getBookPeopleRoleById(id)
                .map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}") //UPDATE
    public ResponseEntity<BookPeopleRole> updateBookPeopleRole(
            @PathVariable Long id,
            @Valid @RequestBody BookPeopleRole bookPeopleRoleDetails){
        Optional<BookPeopleRole>updateBookPeopleRole = bookPeopleRoleService
                .updateBookPeopleRole(id, bookPeopleRoleDetails);
        return updateBookPeopleRole.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}") //DELETE
    public ResponseEntity<Void> deleteBookPeopleRole(@PathVariable Long id){
        bookPeopleRoleService.deleteBookPeopleRole(id);
        return ResponseEntity.noContent().build();
    }

}
