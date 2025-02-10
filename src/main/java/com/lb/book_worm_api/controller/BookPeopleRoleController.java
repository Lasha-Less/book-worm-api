package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.service.BookPeopleRoleService;
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
    public ResponseEntity<BookPeopleRole> createBookPeopleRole(@RequestBody BookPeopleRole bookPeopleRole){
        return ResponseEntity.ok(bookPeopleRoleService.createBookPeopleRole(bookPeopleRole));
    }

    @GetMapping //READ all BookPeopleRoles
    public ResponseEntity<List<BookPeopleRole>> getAllBookPeopleRoles(){
        return ResponseEntity.ok(bookPeopleRoleService.getAllBookPeopleRoles());
    }

    @GetMapping("/{id}") //READ BookPeopleRole
    public ResponseEntity<BookPeopleRole> getBookPeopleRoleById(@PathVariable Long id){
        return bookPeopleRoleService.getBookPeopleRoleById(id)
                .map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}") //UPDATE
    public ResponseEntity<BookPeopleRole> updateBookPeopleRole(
            @PathVariable Long id,
            @RequestBody BookPeopleRole bookPeopleRoleDetails){
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
