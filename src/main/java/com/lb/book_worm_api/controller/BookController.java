package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.dto.BookDTO;
import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.dto.BookUpdateDTO;
import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping //Create or save new book
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookInputDTO bookInputDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookInputDTO));
    }

    @GetMapping // All books
    public ResponseEntity<List<BookDTO>> getAllBooks(){
        List<BookDTO> books = bookService.getAllBooks();
        if (books.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/by-collection/{collectionId}")
    public ResponseEntity<List<BookDTO>> getBooksByCollection(@PathVariable Long collectionId) {
        List<BookDTO> books = bookService.getBooksByCollection(collectionId);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/by-collection-name/{collectionName}")
    public ResponseEntity<List<BookDTO>> getBooksByCollectionName(@PathVariable String collectionName) {
        List<BookDTO> books = bookService.getBooksByCollectionName(collectionName);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<BookDTO>> searchBooks(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String language) {
        List<BookDTO> books = bookService.searchBooks(year, language);
        return ResponseEntity.ok(books);
    }


    @GetMapping("/{id}") // Individual book
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id){
        BookDTO book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookDTO>> getBooksByTitle(@RequestParam String title){
        List<BookDTO> books = bookService.getBooksByTitle(title);
        if (books.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookUpdateDTO updateBookDTO) {
        BookDTO updatedBook = bookService.updateBook(id, updateBookDTO);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{bookId}/collections/{collectionName}")
    public ResponseEntity<String> removeCollectionFromBook(
            @PathVariable Long bookId,
            @PathVariable String collectionName) {

        bookService.removeCollectionFromBook(bookId, collectionName);
        return ResponseEntity.ok("Collection '" + collectionName + "' removed from book with ID " + bookId);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{bookId}/people/{personId}")
    public ResponseEntity<BookDTO> removePersonFromBook(
            @PathVariable Long bookId,
            @PathVariable Long personId) {
        BookDTO updatedBook = bookService.removePersonFromBook(bookId, personId);
        return ResponseEntity.ok(updatedBook);
    }

}
