package com.lb.book_worm_api.util;

import com.lb.book_worm_api.dto.BookDTO;
import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.service.CollectionService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BookConverter {
    private final CollectionService collectionService;

    public BookConverter(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    public Book convertToBookEntity(BookInputDTO bookInputDTO) {
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

}
