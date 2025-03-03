package com.lb.book_worm_api.validation;

import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.exception.DuplicateResourceException;
import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.repository.BookRepo;
import com.lb.book_worm_api.service.CollectionService;
import org.springframework.stereotype.Service;

@Service
public class BookValidator {
    private final BookRepo bookRepo;
    private final CollectionService collectionService;

    public BookValidator(BookRepo bookRepo, CollectionService collectionService) {
        this.bookRepo = bookRepo;
        this.collectionService = collectionService;
    }

    public void validateBookInput(BookInputDTO bookInputDTO) {
        if (bookRepo.existsByTitle(bookInputDTO.getTitle())) {
            throw new DuplicateResourceException(
                    "A book with the title '" + bookInputDTO.getTitle() + "' already exists.");
        }

        if (bookInputDTO.getCollections() == null || bookInputDTO.getCollections().isEmpty()) {
            throw new ValidationException("At least one collection must be specified.");
        }

        // Validate each collection exists in the database
        for (String collectionName : bookInputDTO.getCollections()) {
            if (collectionService.findByName(collectionName).isEmpty()) {
                throw new ValidationException("Collection '" + collectionName + "' does not exist.");
            }
        }

        if (bookInputDTO.getInStock() == null) {
            throw new ValidationException("Stock status must be specified.");
        }

        if ((bookInputDTO.getAuthors() == null || bookInputDTO.getAuthors().isEmpty()) &&
                (bookInputDTO.getEditors() == null || bookInputDTO.getEditors().isEmpty())) {
            throw new ValidationException("A book must have at least one author or editor.");
        }
    }
}
