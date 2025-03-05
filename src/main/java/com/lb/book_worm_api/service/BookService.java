package com.lb.book_worm_api.service;

import com.lb.book_worm_api.dto.*;
import com.lb.book_worm_api.exception.ResourceNotFoundException;
import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.model.*;
import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
import com.lb.book_worm_api.repository.BookRepo;
import com.lb.book_worm_api.repository.BookSpecification;
import com.lb.book_worm_api.repository.PersonRepo;
import com.lb.book_worm_api.util.BookConverter;
import com.lb.book_worm_api.util.BookMapper;
import com.lb.book_worm_api.util.LanguageUtils;
import com.lb.book_worm_api.validation.BookValidator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    @PersistenceContext
    private EntityManager entityManager;

    private final BookRepo bookRepo;
    private final CollectionService collectionService;
    private final PersonService personService;
    private final BookPeopleRoleRepo bookPeopleRoleRepo;
    private final PersonRepo personRepo;
    private final BookValidator bookValidator;
    private final BookConverter bookConverter;
    private final BookPeopleRoleService bookPeopleRoleService;
    private final BookMapper bookMapper;



    public BookService(
            BookRepo bookRepo,
            CollectionService collectionService,
            PersonService personService,
            BookPeopleRoleRepo bookPeopleRoleRepo,
            PersonRepo personRepo,
            BookValidator bookValidator,
            BookConverter bookConverter,
            BookPeopleRoleService bookPeopleRoleService,
            BookMapper bookMapper) {
        this.bookRepo = bookRepo;
        this.collectionService = collectionService;
        this.personService = personService;
        this.bookPeopleRoleRepo = bookPeopleRoleRepo;
        this.personRepo = personRepo;
        this.bookValidator = bookValidator;
        this.bookConverter = bookConverter;
        this.bookPeopleRoleService = bookPeopleRoleService;
        this.bookMapper = bookMapper;
    }

    /**
     * CREATE BOOK
     */
    @Transactional
    public BookDTO createBook(BookInputDTO bookInputDTO) {
        System.out.println("Service bookConverter instance: " + System.identityHashCode(this.bookConverter));
        //DEBUG LINE
        System.out.println("Inside createBook() method");
        bookValidator.validateBookInput(bookInputDTO);
        //DEBUG LINE
        System.out.println("validateBookInput was called");
        System.out.println("bookConverter input DTO: " + bookInputDTO);
        Book book = bookConverter.convertToBookEntity(bookInputDTO);
        //DEBUG LINE
        System.out.println("convertToBookEntity was called");
        System.out.println("convertToBookEntity returned this: " + book);
        logger.debug("Saving book: {}", book);
        System.out.println("Book before saving: " + book);
        Book savedBook = bookRepo.save(book);
        //DEBUG LINE
        System.out.println("bookRepo.save() was called");
        System.out.println("Saved Book returned by bookRepo.save(): " + savedBook);
        if (savedBook == null) {
            throw new RuntimeException("Mocked bookRepo.save() returned null!");
        }
        System.out.println("Saved Book returned by bookRepo.save(): " + savedBook + savedBook.getId());
        logger.debug("Saved book ID: {}", savedBook.getId());
        logger.debug("Book object before saving: {}", book);


        bookPeopleRoleService.assignPeopleToBook(savedBook, bookInputDTO);
        //DEBUG LINE
        System.out.println("assignPeopleToBook() was called");
        bookRepo.flush();

        return fetchAndConvertToDTO(savedBook.getId());
    }



    private BookDTO fetchAndConvertToDTO(Long bookId) {
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found after saving"));
        entityManager.refresh(book);
        return bookMapper.convertToDTO(book);
    }

    /**
     * READ BOOK
     */
    //GET all books
    public List<BookDTO> getAllBooks() {
        return bookRepo.findAll().stream()
                .map(bookMapper::convertToDTO) // Convert each Book entity to BookDTO
                .collect(Collectors.toList());
    }


    //GET books by collection
    public List<BookDTO> getBooksByCollection(Long collectionId) {
        List<Book> books = bookRepo.findByCollectionsId(collectionId);
        return books.stream().map(book -> {
            BookDTO dto = new BookDTO();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setOriginalLanguage(book.getLingo());
            dto.setFormat(book.getFormat());
            dto.setInStock(book.isInStock());
            List<String>collectionNames = book.getCollections().stream().map(Collection::getName).toList();
            dto.setCollections(collectionNames);
            return dto;
        }).collect(Collectors.toList());
    }



    public List<BookDTO> getBooksByCollectionName(String collectionName) {
        if (collectionName.length() < 3) {
            throw new IllegalArgumentException("Collection name must be at least 3 characters long.");
        }

        List<Book> books = bookRepo.findByCollectionNameLike(collectionName);
        return books.stream().map(book -> {
            BookDTO dto = new BookDTO();
            dto.setId(book.getId());
            dto.setTitle(book.getTitle());
            dto.setLingo(book.getLingo());
            dto.setFormat(book.getFormat());
            dto.setInStock(book.isInStock());

            // Convert Collection entities to a list of their names
            List<String> collectionNames = book.getCollections().stream()
                    .map(Collection::getName)
                    .collect(Collectors.toList());
            dto.setCollections(collectionNames);

            return dto;
        }).collect(Collectors.toList());
    }



    public List<BookDTO> searchBooks(Integer year, String language) {
        Specification<Book> spec = BookSpecification.filterByCriteria(year, language);
        return bookRepo.findAll(spec).stream()
                .map(bookMapper::convertToDTO)
                .collect(Collectors.toList());
    }


    //GET single
    public BookDTO getBookById(Long id) {
        Book book = bookRepo.findByIdWithCollections(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        Hibernate.initialize(book.getCollections());
        //start DEBUG
        logger.debug("Book retrieved: {}", book.getTitle());
        logger.debug("Collections retrieved from DB after explicit loading: {}", book.getCollections().size());
        //end DEBUG
        return bookMapper.convertToDTO(book);
    }



    public List<BookDTO> getBooksByTitle(String title) {
        List<Book> books = bookRepo.findByTitleContainingIgnoreCase(title);
        return books.stream()
                .map(bookMapper::convertToDTO)
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

        // Save book to ensure it's managed by JPA
        bookRepo.save(book); // Removed flush here

        // Update collections
        updateBookCollections(book, updateDTO);

        // Update people (authors, editors, others)
        updateBookPeople(book, updateDTO);

        // Persist final changes before fetching fresh data
        bookRepo.flush();

        // Fetch fresh book data and convert to DTO
        return fetchAndConvertToDTO(bookId);
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
        if (updateDTO.getCollections() == null) {
            logger.info("No new collections provided, skipping update.");
            return;
        }
        // Retrieve existing collections
        Set<Collection> existingCollections = new HashSet<>(book.getCollections());
        // Find new collections from database
        List<Collection> collectionsToAdd = collectionService.findAllByName(updateDTO.getCollections());
        // Ensure all collections exist
        if (collectionsToAdd.size() != updateDTO.getCollections().size()) {
            throw new ValidationException("One or more collections do not exist.");
        }
        // Merge existing collections with new ones
        existingCollections.addAll(collectionsToAdd);
        // ✅ Instead of clearing, explicitly update the reference with merged collections
        book.setCollections(existingCollections);
        bookRepo.save(book); // ✅ Ensure the book is saved with updated collections
        bookRepo.flush(); // ✅ Persist changes
        logger.info("DEBUG MESSAGE. Final collections assigned to book: {}", book.getCollections().size());
    }



    public void removeCollectionFromBook(Long bookId, String collectionName) {
        // Retrieve the book by ID or throw an exception if not found
        Book book = bookRepo.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + bookId));
        // Retrieve the collection by name or throw an exception if not found
        Collection collection = collectionService.findByName(collectionName)
                .orElseThrow(() -> new ResourceNotFoundException("Collection not found with name: " + collectionName));
        // A) Check if the collection is linked to the book
        if (!book.getCollections().contains(collection)) {
            throw new ValidationException("Book is not linked to the collection: " + collectionName);
        }
        // B) Remove the collection from the book's collection set
        book.getCollections().remove(collection);
        logger.info("Collection removed from book: {}", collectionName);
        // C) If no collection is left, assign "Unsorted"
        if (book.getCollections().isEmpty()) {
            Collection unsortedCollection = collectionService.findByName("Unsorted")
                    .orElseThrow(() -> new ResourceNotFoundException("Unsorted collection not found!"));
            book.getCollections().add(unsortedCollection);
            logger.info("No collections left. Assigned 'Unsorted' collection to book.");
        }
        // D) Save and persist changes
        bookRepo.save(book);
        bookRepo.flush();
    }



    private void updateBookPeople(Book book, BookUpdateDTO updateDTO) {
        bookPeopleRoleService.updateBookPeople(book, updateDTO);
    }



    @Transactional
    public BookDTO removePersonFromBook(Long bookId, Long personId) {
        Book book = bookRepo.findByIdWithCollections(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));
        // Find all roles linked to this book for the given person
        List<BookPeopleRole> bookRoles = book.getBookPeopleRoles().stream()
                .filter(role -> role.getPerson().getId().equals(personId))
                .collect(Collectors.toList());
        if (bookRoles.isEmpty()) {
            throw new ResourceNotFoundException("Person with id " + personId + " is not related to this book.");
        }
        // Count remaining authors and editors
        long remainingAuthors = book.getBookPeopleRoles().stream()
                .filter(role -> role.getRole() == Role.AUTHOR)
                .count();
        long remainingEditors = book.getBookPeopleRoles().stream()
                .filter(role -> role.getRole() == Role.EDITOR)
                .count();
        for (BookPeopleRole bookRole : bookRoles) {
            // If removing an AUTHOR or EDITOR, ensure at least one remains
            if ((bookRole.getRole() == Role.AUTHOR && remainingAuthors <= 1) ||
                    (bookRole.getRole() == Role.EDITOR && remainingEditors <= 1)) {
                throw new ValidationException("Cannot remove the last author or editor from the book.");
            }
            // ✅ Remove the role directly here to avoid circular dependency
            book.getBookPeopleRoles().remove(bookRole);
            bookPeopleRoleRepo.delete(bookRole); // Hard delete
        }
        // ✅ Call method in PersonService to remove orphaned person
        personService.removeOrphanedPerson(personId);
        // Persist changes before fetching updated data
        bookRepo.flush();
        return fetchAndConvertToDTO(bookId); // Return updated book details
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

}
