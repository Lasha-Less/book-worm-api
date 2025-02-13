package com.lb.book_worm_api.service;

import com.lb.book_worm_api.dto.BookDTO;
import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.dto.BookRoleDTO;
import com.lb.book_worm_api.dto.PersonRoleDTO;
import com.lb.book_worm_api.exception.DuplicateResourceException;
import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.repository.BookRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final BookRepo bookRepo;
    private final CollectionService collectionService;
    private final PersonService personService;

    public BookService(BookRepo bookRepo, CollectionService collectionService, PersonService personService) {
        this.bookRepo = bookRepo;
        this.collectionService = collectionService;
        this.personService = personService;
    }

    //GET all books
    public List<BookDTO> getAllBooks() {
        return bookRepo.findAll().stream()
                .map(this::convertToDTO) // Convert each Book entity to BookDTO
                .collect(Collectors.toList());
    }


    //GET single
    public BookDTO getBookById(Long id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        return convertToDTO(book);
    }


    public List<BookDTO> getBooksByTitle(String title) {
        List<Book> books = bookRepo.findByTitleContainingIgnoreCase(title);
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    //CREATE single
    @Transactional
    public BookDTO createBook(BookInputDTO bookInputDTO) {
        if (bookRepo.existsByTitle(bookInputDTO.getTitle())) {
            throw new DuplicateResourceException(
                    "A book with the title '" + bookInputDTO.getTitle() + "' already exists.");
        }

        // Defensive validation
        if (bookInputDTO.getCollections() == null || bookInputDTO.getCollections().isEmpty()) {
            throw new ValidationException("At least one collection must be specified.");
        }

        if (bookInputDTO.getInStock() == null) {
            throw new ValidationException("Stock status must be specified.");
        }

        // Validate collections
        Set<Collection> validatedCollections = bookInputDTO.getCollections().stream()
                .map(collectionName -> collectionService.findByName(collectionName)
                        .orElseThrow(() -> new ValidationException("Collection '" + collectionName +
                                "' does not exist. Please choose an existing collection or create a new one.")))
                .collect(Collectors.toSet());

        // Convert DTO to Book entity
        Book book = new Book();
        book.setTitle(bookInputDTO.getTitle());
        book.setLingo(bookInputDTO.getLanguage());
        book.setFormat(bookInputDTO.getFormat());
        book.setLocation(bookInputDTO.getLocation());
        book.setInStock(bookInputDTO.getInStock());
        book.setOriginalLanguage(bookInputDTO.getOriginalLanguage());
        book.setPublicationYear(bookInputDTO.getPublicationYear());
        book.setHistoricalDate(bookInputDTO.getHistoricalDate());
        book.setPublisher(bookInputDTO.getPublisher());
        book.setCollections(validatedCollections);

        // Save book first (so it has an ID)
        Book savedBook = bookRepo.save(book);

        // Assign authors, editors, etc.
        personService.assignPeopleToBook(savedBook, bookInputDTO);

        // Convert and return DTO
        return convertToDTO(savedBook);
    }

    //UPDATE single
    @Transactional
    public Optional<BookDTO> updateBook(Long id, BookInputDTO bookInputDTO) {
        return bookRepo.findById(id).map(existingBook -> {
            boolean isUpdated = false;

            if (bookInputDTO.getTitle() != null && !bookInputDTO.getTitle().isBlank()) {
                existingBook.setTitle(bookInputDTO.getTitle());
                isUpdated = true;
            }
            if (bookInputDTO.getLanguage() != null && !bookInputDTO.getLanguage().isBlank()) {
                existingBook.setLingo(bookInputDTO.getLanguage());
                isUpdated = true;
            }
            if (bookInputDTO.getFormat() != null && !bookInputDTO.getFormat().isBlank()) {
                existingBook.setFormat(bookInputDTO.getFormat());
                isUpdated = true;
            }
            if (bookInputDTO.getLocation() != null && !bookInputDTO.getLocation().isBlank()) {
                existingBook.setLocation(bookInputDTO.getLocation());
                isUpdated = true;
            }
            if (bookInputDTO.getInStock() != null) {
                existingBook.setInStock(bookInputDTO.getInStock());
                isUpdated = true;
            }
            if (bookInputDTO.getOriginalLanguage() != null && !bookInputDTO.getOriginalLanguage().isBlank()) {
                existingBook.setOriginalLanguage(bookInputDTO.getOriginalLanguage());
                isUpdated = true;
            }
            if (bookInputDTO.getPublicationYear() != null) {
                existingBook.setPublicationYear(bookInputDTO.getPublicationYear());
                isUpdated = true;
            }
            if (bookInputDTO.getHistoricalDate() != null) {
                existingBook.setHistoricalDate(bookInputDTO.getHistoricalDate());
                isUpdated = true;
            }
            if (bookInputDTO.getPublisher() != null && !bookInputDTO.getPublisher().isBlank()) {
                existingBook.setPublisher(bookInputDTO.getPublisher());
                isUpdated = true;
            }

            // Validate and update collections
            if (bookInputDTO.getCollections() != null && !bookInputDTO.getCollections().isEmpty()) {
                Set<Collection> validatedCollections = bookInputDTO.getCollections().stream()
                        .map(collectionName -> collectionService.findByName(collectionName)
                                .orElseThrow(() -> new ValidationException("Collection '" + collectionName +
                                        "' does not exist. Please choose an existing collection or create a new one.")))
                        .collect(Collectors.toSet());

                existingBook.setCollections(validatedCollections);
                isUpdated = true;
            }

            // Save only if something was updated
            if (isUpdated) {
                Book updatedBook = bookRepo.save(existingBook);
                personService.assignPeopleToBook(updatedBook, bookInputDTO);
                return convertToDTO(updatedBook);
            }

            return convertToDTO(existingBook); // No changes, return as is
        });
    }

    //DELETE single
    @Transactional
    public void deleteBook(Long id){
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cannot delete: Book with ID " + id + " does not exist."));

        bookRepo.delete(book);
    }

    public BookDTO convertToDTO(Book book) {
        BookDTO bookDTO = new BookDTO();

        bookDTO.setId(book.getId());
        bookDTO.setTitle(book.getTitle());
        bookDTO.setLingo(book.getLingo());
        bookDTO.setFormat(book.getFormat());
        bookDTO.setLocation(book.getLocation());
        bookDTO.setInStock(book.isInStock());
        bookDTO.setOriginalLanguage(book.getOriginalLanguage());
        bookDTO.setPublicationYear(book.getPublicationYear());
        bookDTO.setHistoricalDate(book.getHistoricalDate());
        bookDTO.setPublisher(book.getPublisher());

        // Convert collections
        bookDTO.setCollections(
                book.getCollections().stream()
                        .map(Collection::getName)
                        .collect(Collectors.toList())
        );

        // Convert book roles properly
        bookDTO.setPeople(
                book.getBookPeopleRoles().stream()
                        .map(role -> new PersonRoleDTO(
                                role.getPerson().getFirstName(), role.getPerson().getLastName(), // Combine full name
                                role.getRole().getDisplayName() // Convert Role Enum to String
                        ))
                        .collect(Collectors.toList())
        );

        return bookDTO;
    }


}
