package com.lb.book_worm_api.service;

import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.repository.BookRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepo bookRepo;

    public BookService(BookRepo bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> getAllBooks(){
        return bookRepo.findAll();
    }

    public Optional<Book> getBookById(Long id){
        return bookRepo.findById(id);
    }

    public Book saveBook(Book book){
        return bookRepo.save(book);
    }

    public Optional<Book> updateBook(Long id, Book bookDetails){
        return bookRepo.findById(id).map(existingBook-> {
            existingBook.setTitle(bookDetails.getTitle());
            existingBook.setLingo(bookDetails.getLingo());
            existingBook.setFormat(bookDetails.getFormat());
            existingBook.setInStock(bookDetails.isInStock());
            existingBook.setLocation(bookDetails.getLocation());
            existingBook.setPublicationYear(bookDetails.getPublicationYear());
            existingBook.setHistoricalDate(bookDetails.getHistoricalDate());
            existingBook.setOriginalLanguage(bookDetails.getOriginalLanguage());
            existingBook.setPublisher(bookDetails.getPublisher());
            return bookRepo.save(existingBook);
        });
    }

    public boolean deleteBook(Long id){
        if (bookRepo.existsById(id)) {
            bookRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
