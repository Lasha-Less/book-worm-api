package com.lb.book_worm_api.service;
import com.lb.book_worm_api.dto.BookDTO;
import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.dto.PersonRoleInputDTO;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
import com.lb.book_worm_api.repository.BookRepo;
import com.lb.book_worm_api.repository.PersonRepo;
import com.lb.book_worm_api.util.BookConverter;
import com.lb.book_worm_api.util.BookMapper;
import com.lb.book_worm_api.validation.BookValidator;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import com.lb.book_worm_api.model.Book;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    private BookService bookService;

    // 🔹 Mock Dependencies
    @Mock private BookRepo bookRepo;
    @Mock private CollectionService collectionService;
    @Mock private PersonService personService;
    @Mock private BookPeopleRoleRepo bookPeopleRoleRepo;
    @Mock private PersonRepo personRepo;
    @Mock private BookValidator bookValidator;
    @Mock private BookConverter bookConverter;
    @Mock private BookMapper bookMapper;
    @Mock private BookPeopleRoleService bookPeopleRoleService;
    @Mock private EntityManager entityManager; // Mocking EntityManager

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);

        bookService = new BookService(
                bookRepo,
                collectionService,
                personService,
                bookPeopleRoleRepo,
                personRepo,
                bookValidator,
                bookConverter,  // ✅ Inject the mocked bookConverter
                bookPeopleRoleService,
                bookMapper
        );

        ReflectionTestUtils.setField(bookService, "bookConverter", bookConverter);
        //Because EntityManager is injected via @PersistenceContext, we need to manually set it
        ReflectionTestUtils.setField(bookService, "entityManager", entityManager);
    }


    @Test
    void createBook_Test() {
        // Arrange: Create a sample BookInputDTO
        BookInputDTO bookInputDTO = new BookInputDTO(
                "Test Book",
                List.of(new PersonRoleInputDTO("Adam", null, "Brown", null)),
                List.of(),
                "en",
                "paperback",
                "library",
                true,
                List.of("Fiction"));

        // ✅ Define the mock Book once
        Book mockBook = new Book();
        mockBook.setId(1L);
        mockBook.setTitle("Test Book");
        mockBook.setLingo("en");
        mockBook.setFormat("paperback");
        mockBook.setLocation("library");
        mockBook.setInStock(true);

        BookDTO mockBookDTO = new BookDTO();
        mockBookDTO.setId(1L);
        mockBookDTO.setTitle("Test Book");

        // Mock behavior to return the same Book instance consistently
        when(bookConverter.convertToBookEntity(any())).thenReturn(mockBook);
        when(bookRepo.save(any(Book.class))).thenReturn(mockBook);
        when(bookRepo.findById(anyLong())).thenReturn(Optional.of(mockBook)); // ✅ Reuse mockBook here
        when(bookMapper.convertToDTO(any(Book.class))).thenReturn(mockBookDTO); // ✅ Mock DTO conversion

        System.out.println("Test bookConverter instance: " + System.identityHashCode(bookConverter));

        // Act: Call createBook()
        bookService.createBook(bookInputDTO);

        // ✅ Assert: Verify that validation was called
        verify(bookValidator, times(1)).validateBookInput(bookInputDTO);

        // ✅ Assert: Verify that bookConverter.convertToBookEntity() was called
        verify(bookConverter, times(1)).convertToBookEntity(bookInputDTO);

        // ✅ Assert: Verify that bookRepo.save() was called with a non-null book
        verify(bookRepo, times(1)).save(any(Book.class));

        // ✅ Assert: Verify that bookPeopleRoleService.assignPeopleToBook() was called
        verify(bookPeopleRoleService, times(1)).assignPeopleToBook(mockBook, bookInputDTO);

        // ✅ Assert: Verify that bookRepo.flush() was called
        verify(bookRepo, times(1)).flush();

        // ✅ Assert: Verify that entityManager.refresh() was called on the saved book
        verify(entityManager, times(1)).refresh(mockBook);

        // ✅ Assert: Verify that bookRepo.findById() was called with the saved book's ID
        verify(bookRepo, times(1)).findById(mockBook.getId());

        // ✅ Assert: Verify that bookMapper.convertToDTO() was called on the saved book
        verify(bookMapper, times(1)).convertToDTO(mockBook);
    }



}