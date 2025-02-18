package com.lb.book_worm_api.service;

import com.lb.book_worm_api.dto.*;
import com.lb.book_worm_api.exception.DuplicateResourceException;
import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.model.*;
import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
import com.lb.book_worm_api.repository.BookRepo;
import com.lb.book_worm_api.repository.PersonRepo;
import com.lb.book_worm_api.util.LanguageUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    @PersistenceContext
    private EntityManager entityManager;

    private final BookRepo bookRepo;
    private final CollectionService collectionService;
    private final PersonService personService;
    private final BookPeopleRoleRepo bookPeopleRoleRepo;
    private final PersonRepo personRepo;

    public BookService(
            BookRepo bookRepo,
            CollectionService collectionService,
            PersonService personService,
            BookPeopleRoleRepo bookPeopleRoleRepo,
            PersonRepo personRepo) {
        this.bookRepo = bookRepo;
        this.collectionService = collectionService;
        this.personService = personService;
        this.bookPeopleRoleRepo = bookPeopleRoleRepo;
        this.personRepo = personRepo;
    }

    /**
     * CREATE BOOK
     */
    @Transactional
    public BookDTO createBook(BookInputDTO bookInputDTO) {
        validateBookInput(bookInputDTO);

        Book book = convertToBookEntity(bookInputDTO);
        Book savedBook = bookRepo.save(book);

        assignPeopleToBook(savedBook, bookInputDTO);
        bookRepo.flush();

        return fetchAndConvertToDTO(savedBook.getId());
    }

    private void validateBookInput(BookInputDTO bookInputDTO) {
        if (bookRepo.existsByTitle(bookInputDTO.getTitle())) {
            throw new DuplicateResourceException(
                    "A book with the title '" + bookInputDTO.getTitle() + "' already exists.");
        }

        if (bookInputDTO.getCollections() == null || bookInputDTO.getCollections().isEmpty()) {
            throw new ValidationException("At least one collection must be specified.");
        }

        if (bookInputDTO.getInStock() == null) {
            throw new ValidationException("Stock status must be specified.");
        }

        if ((bookInputDTO.getAuthors() == null || bookInputDTO.getAuthors().isEmpty()) &&
                (bookInputDTO.getEditors() == null || bookInputDTO.getEditors().isEmpty())) {
            throw new ValidationException("A book must have at least one author or editor.");
        }
    }

    private Book convertToBookEntity(BookInputDTO bookInputDTO) {
        Set<Collection> validatedCollections = bookInputDTO.getCollections().stream()
                .map(collectionService::findByName)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

        if (validatedCollections.size() != bookInputDTO.getCollections().size()) {
            throw new ValidationException("Some collections do not exist.");
        }

        Book book = new Book();
        book.setTitle(bookInputDTO.getTitle());
        book.setLingo(LanguageUtils.toIsoCode(bookInputDTO.getLanguage()));
        book.setFormat(bookInputDTO.getFormat());
        book.setLocation(bookInputDTO.getLocation());
        book.setInStock(bookInputDTO.getInStock());
        book.setOriginalLanguage(LanguageUtils.toIsoCode(bookInputDTO.getOriginalLanguage()));
        book.setPublicationYear(bookInputDTO.getPublicationYear());
        book.setHistoricalDate(bookInputDTO.getHistoricalDate());
        book.setPublisher(bookInputDTO.getPublisher());
        book.setCollections(validatedCollections);

        return book;
    }

    private void assignPeopleToBook(Book book, BookInputDTO bookInputDTO) {
        personService.assignPeopleToBook(book, bookInputDTO.getAuthors(), Role.AUTHOR);
        personService.assignPeopleToBook(book, bookInputDTO.getEditors(), Role.EDITOR);
        personService.assignPeopleToBook(book, bookInputDTO.getOthers(), Role.OTHER);
    }

    private BookDTO fetchAndConvertToDTO(Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found after saving"));

        entityManager.refresh(book);
        return convertToDTO(book);
    }

    /**
     * READ BOOK
     */
    //GET all books
    public List<BookDTO> getAllBooks() {
        return bookRepo.findAll().stream()
                .map(this::convertToDTO) // Convert each Book entity to BookDTO
                .collect(Collectors.toList());
    }


    //GET single
    public BookDTO getBookById(Long id) {
        Book book = bookRepo.findByIdWithCollections(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        Hibernate.initialize(book.getCollections());
        //start DEBUG
        System.out.println("Book retrieved: " + book.getTitle());
        System.out.println("Collections retrieved from DB after explicit loading: " + book.getCollections().size());
        //end DEBUG
        return convertToDTO(book);
    }


    public List<BookDTO> getBooksByTitle(String title) {
        List<Book> books = bookRepo.findByTitleContainingIgnoreCase(title);
        return books.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * UPDATE BOOK
     */
    @Transactional
    public BookDTO updateBook(Long bookId, BookUpdateDTO updateDTO) {
        Book book = bookRepo.findByIdWithCollections(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        // Update book attributes
        updateBookAttributes(book, updateDTO);

        // Force saving the book first to ensure it is managed by JPA
        book = bookRepo.save(book);
        bookRepo.flush(); // Persist book before modifying collections

        // Update collections
        updateBookCollections(book, updateDTO);

        // Update people (authors, editors, others)
        updateBookPeople(book, updateDTO);

        // Persist final changes
        book = bookRepo.save(book);
        bookRepo.flush();
        entityManager.refresh(book); // Ensure all updates are applied

        // Debugging
        System.out.println("Final collections in book after persistence: " + book.getCollections().size());

        return convertToDTO(book);
    }

    private void updateBookAttributes(Book book, BookUpdateDTO updateDTO) {
        if (updateDTO.getTitle() != null) book.setTitle(updateDTO.getTitle());
        if (updateDTO.getLanguage() != null) book.setLingo(LanguageUtils.toIsoCode(updateDTO.getLanguage()));
        if (updateDTO.getFormat() != null) book.setFormat(updateDTO.getFormat());
        if (updateDTO.getLocation() != null) book.setLocation(updateDTO.getLocation());
        if (updateDTO.getInStock() != null) book.setInStock(updateDTO.getInStock());
        if (updateDTO.getOriginalLanguage() != null)
            book.setOriginalLanguage(LanguageUtils.toIsoCode(updateDTO.getOriginalLanguage()));
        if (updateDTO.getPublicationYear() != null) book.setPublicationYear(updateDTO.getPublicationYear());
        if (updateDTO.getHistoricalDate() != null) book.setHistoricalDate(updateDTO.getHistoricalDate());
        if (updateDTO.getPublisher() != null) book.setPublisher(updateDTO.getPublisher());
    }

    private void updateBookCollections(Book book, BookUpdateDTO updateDTO) {
        if (updateDTO.getCollections() == null) return;

        System.out.println("Existing collections before update: " + book.getCollections().size());

        List<Collection> collections = collectionService.findAllByName(updateDTO.getCollections());

        System.out.println("Collections found: " + collections.size());

        if (collections.size() != updateDTO.getCollections().size()) {
            throw new ValidationException("One or more collections do not exist.");
        }

        book.getCollections().clear(); // Prevent replacing, ensure fresh update
        bookRepo.flush(); // Persist cleared state

        book.getCollections().addAll(collections);

        System.out.println("Collections assigned to book: " + book.getCollections().size());
    }

    private void updateBookPeople(Book book, BookUpdateDTO updateDTO) {
        if (updateDTO.getAuthors() != null) {
            personService.assignPeopleToBook(book, updateDTO.getAuthors(), Role.AUTHOR);
        }
        if (updateDTO.getEditors() != null) {
            personService.assignPeopleToBook(book, updateDTO.getEditors(), Role.EDITOR);
        }
        if (updateDTO.getOthers() != null) {
            personService.assignPeopleToBook(book, updateDTO.getOthers(), Role.OTHER);
        }
    }



    /**
     * DELETE BOOK
     */
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        // Delete all role assignments related to this book
        bookPeopleRoleRepo.deleteRolesByBookId(id);

        // Delete the book itself
        bookRepo.delete(book);

        // Remove people who are no longer associated with any books
        List<Person> orphanedPeople = personRepo.findAll().stream()
                .filter(person -> person.getBookPeopleRoles().isEmpty()) // Find people with no books
                .toList();

        personRepo.deleteAll(orphanedPeople);
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

        bookDTO.setAuthors(getPeopleByRole(book, Role.AUTHOR));
        bookDTO.setEditors(getPeopleByRole(book, Role.EDITOR));
        bookDTO.setOthers(getPeopleByRole(book, Role.OTHER));

        // Convert collections
        bookDTO.setCollections(
                new ArrayList<>(book.getCollections()).stream() // Force loading collections
                        .map(Collection::getName)
                        .collect(Collectors.toList())
        );

        return bookDTO;
    }

    private List<PersonRoleDTO> getPeopleByRole(Book book, Role role) {
        //start DEBUGGING CODE (Remove when no longer needed)
        System.out.println("Book ID: " + book.getId() + " Role: " + role +
                " People Roles Found: " + book.getBookPeopleRoles().size());
        //end DEBUGGING CODE

        return book.getBookPeopleRoles().stream()
                .filter(bp -> bp.getRole().equals(role)) // Filter by role
                .map(bp -> new PersonRoleDTO(bp.getPerson()
                        .getFirstName(), bp.getPerson().getLastName(), role.name()))
                .collect(Collectors.toList());
    }



}
