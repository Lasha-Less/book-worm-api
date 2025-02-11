package com.lb.book_worm_api.controller;

import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.model.Book;
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
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookInputDTO bookInputDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(bookInputDTO));
    }

    @GetMapping // All books
    public ResponseEntity<List<Book>> getAllBooks(){
        List<Book>books = bookService.getAllBooks();
        if (books.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}") // Individual book
    public ResponseEntity<Book> getBookById(@PathVariable Long id){
        Book book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Book>> getBooksByTitle(@RequestParam String title){
        List<Book> books = bookService.getBooksByTitle(title);
        if (books.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(books);
    }

    @PutMapping("/{id}") //update
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody BookInputDTO bookInputDTO){
        Optional<Book> updatedBook = bookService.updateBook(id, bookInputDTO);
        return updatedBook.map(ResponseEntity::ok).orElseGet(()-> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

}
