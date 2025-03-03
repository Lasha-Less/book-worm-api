package com.lb.book_worm_api.service;

import com.lb.book_worm_api.dto.BookDTO;
import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.dto.PersonRoleInputDTO;
import com.lb.book_worm_api.exception.DuplicateResourceException;
import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
import com.lb.book_worm_api.repository.BookRepo;
import com.lb.book_worm_api.repository.PersonRepo;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private BookRepo bookRepo;

    @Mock
    private CollectionService collectionService;

    @Mock
    private PersonService personService;

    @Mock
    BookPeopleRoleRepo bookPeopleRoleRepo;

    @Mock
    private PersonRepo personRepo;

    @InjectMocks
    private BookService bookService;

    private BookInputDTO bookInputDTO;
    private Book book;
    private Collection collection;
    private List<PersonRoleInputDTO> authors;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(bookService, "entityManager", entityManager);
        ReflectionTestUtils.setField(bookService, "collectionService", collectionService);

        collection = new Collection("Fiction");
        authors = List.of(new PersonRoleInputDTO("John", null, "Doe", "AUTHOR"));
        bookInputDTO = new BookInputDTO(
                "Stories Of Testing",
                authors,
                null,
                "English",
                "Hardcover",
                "Libr-A",
                true,
                List.of("Fiction"));

        book = new Book();
        book.setTitle(bookInputDTO.getTitle());
        book.setLingo("en");
        book.setFormat(bookInputDTO.getFormat());
        book.setLocation(bookInputDTO.getLocation());
        book.setInStock(bookInputDTO.getInStock());
        book.setCollections(Collections.singleton(collection));
    }

    @Test
    void shouldThrowExceptionIfBookTitleAlreadyExists() {
        // Mock repository to return true (book title already exists)
        when(bookRepo.existsByTitle(bookInputDTO.getTitle())).thenReturn(true);

        // Assert that DuplicateResourceException is thrown
        Exception exception = assertThrows(DuplicateResourceException.class,
                () -> bookService.createBook(bookInputDTO));

        assertEquals("A book with the title 'Stories Of Testing' already exists.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfNoCollectionsProvided() {
        // Create a book input without collections
        bookInputDTO = new BookInputDTO(
                "Stories Of Testing",
                authors,
                null,
                "English",
                "Hardcover",
                "Libr-A",
                true,
                Collections.emptyList() // No collections
        );

        // Assert that ValidationException is thrown
        Exception exception = assertThrows(ValidationException.class,
                () -> bookService.createBook(bookInputDTO));

        assertEquals("Some collections do not exist.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfStockStatusIsNull() {
        // Create a book input without inStock value
        bookInputDTO = new BookInputDTO(
                "Stories Of Testing",
                authors,
                null,
                "English",
                "Hardcover",
                "Libr-A",
                null, // Missing inStock status
                List.of("Fiction")
        );

        // Assert that ValidationException is thrown
        Exception exception = assertThrows(ValidationException.class,
                () -> bookService.createBook(bookInputDTO));

        assertEquals("Stock status must be specified.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionIfCollectionDoesNotExist() {
        // Mock collectionService to return empty (collection does not exist)
        when(collectionService.findByName("Fiction")).thenReturn(Optional.empty());

        // Assert that ValidationException is thrown when calling createBook
        Exception exception = assertThrows(ValidationException.class,
                () -> bookService.createBook(bookInputDTO));

        assertEquals("Some collections do not exist.", exception.getMessage());

        // Verify collectionService.findByName() was called
        verify(collectionService, times(1)).findByName("Fiction");
    }

    @Test
    void shouldCreateBookSuccessfully() {

        // Mock collectionService to return a valid Collection
        when(collectionService.findByName("Fiction")).thenReturn(Optional.of(collection));

        // Mock bookRepo.save(...) to return a Book with an ID
        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle(bookInputDTO.getTitle());
        savedBook.setLingo("en"); // Assuming LanguageUtils.toIsoCode() returns "en"
        savedBook.setFormat(bookInputDTO.getFormat());
        savedBook.setLocation(bookInputDTO.getLocation());
        savedBook.setInStock(bookInputDTO.getInStock());
        savedBook.setCollections(Collections.singleton(collection));

        // Simulate repository behavior using a map
        Map<Long, Book> fakeDatabase = new HashMap<>();

        // Mock bookRepo.save(...) to store books in the fake "database"
        when(bookRepo.save(any(Book.class))).thenAnswer(invocation -> {
            Book bookToSave = invocation.getArgument(0);
            bookToSave.setId(1L); // Simulate database-generated ID
            fakeDatabase.put(1L, bookToSave); // Store it in the fake database
            return bookToSave;
        });

        // Mock bookRepo.findById(...) to retrieve books from the fake "database"
        when(bookRepo.findById(1L)).thenAnswer(invocation -> Optional.ofNullable(fakeDatabase.get(1L)));

        // Mock entityManager.refresh() to do nothing
        doNothing().when(entityManager).refresh(any(Book.class));

        // Call createBook (which internally calls convertToBookEntity)
        BookDTO createdBookDTO = bookService.createBook(bookInputDTO);

        // Verify the returned BookDTO properties
        assertNotNull(createdBookDTO);
        assertEquals(bookInputDTO.getTitle(), createdBookDTO.getTitle());
        assertEquals("en", createdBookDTO.getLingo());
        assertEquals(bookInputDTO.getFormat(), createdBookDTO.getFormat());
        assertEquals(bookInputDTO.getLocation(), createdBookDTO.getLocation());
        assertEquals(bookInputDTO.getInStock(), createdBookDTO.isInStock());

        // Verify that collectionService.findByName() was called
        verify(collectionService, times(1)).findByName("Fiction");

        // Verify that bookRepo.save() and bookRepo.findById() were called
        verify(bookRepo, times(1)).save(any(Book.class));
        verify(bookRepo, times(1)).findById(1L);

        // Verify that entityManager.refresh() was called
        verify(entityManager, times(1)).refresh(savedBook);
    }

    @Test
    void shouldReturnCollectionWhenFindByNameIsCalled() {
        // Mock collectionService to return a valid Collection
        when(collectionService.findByName("Fiction")).thenReturn(Optional.of(collection));

        // Call the method and capture the result
        Optional<Collection> result = collectionService.findByName("Fiction");

        // Verify the expected collection is returned
        assertTrue(result.isPresent());
        assertEquals("Fiction", result.get().getName());

        // Verify that findByName was actually called once
        verify(collectionService, times(1)).findByName("Fiction");
    }

    @Test
    void shouldSaveAndRetrieveBookFromRepositoryMock() {
        // Simulate repository behavior using a map
        Map<Long, Book> fakeDatabase = new HashMap<>();

        // Mock bookRepo.save(...) to store books in the fake "database"
        when(bookRepo.save(any(Book.class))).thenAnswer(invocation -> {
            Book bookToSave = invocation.getArgument(0);
            bookToSave.setId(1L); // Simulate database-generated ID
            fakeDatabase.put(1L, bookToSave); // Store it in the fake database
            return bookToSave;
        });

        // Mock bookRepo.findById(...) to retrieve books from the fake "database"
        when(bookRepo.findById(1L)).thenAnswer(invocation -> Optional.ofNullable(fakeDatabase.get(1L)));

        // Create a test book
        Book testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setLingo("en");
        testBook.setFormat("Hardcover");
        testBook.setLocation("Library A");
        testBook.setInStock(true);

        // Save the book using the mocked repository
        Book savedBook = bookRepo.save(testBook);

        // Retrieve the saved book
        Optional<Book> retrievedBook = bookRepo.findById(1L);

        // Assertions
        assertNotNull(savedBook);
        assertEquals(1L, savedBook.getId());
        assertTrue(retrievedBook.isPresent());
        assertEquals("Test Book", retrievedBook.get().getTitle());
    }




}