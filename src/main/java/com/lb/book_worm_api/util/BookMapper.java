package com.lb.book_worm_api.util;

import com.lb.book_worm_api.dto.BookDTO;
import com.lb.book_worm_api.dto.PersonRoleDTO;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.Collection;
import com.lb.book_worm_api.model.Role;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookMapper {
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
        bookDTO.setOthers(getNonAuthorEditorRoles(book));

        bookDTO.setCollections(
                book.getCollections().stream().map(Collection::getName).toList()
        );

        return bookDTO;
    }

    private List<PersonRoleDTO> getNonAuthorEditorRoles(Book book) {
        return book.getBookPeopleRoles().stream()
                .filter(bp -> bp.getRole() != Role.AUTHOR && bp.getRole() != Role.EDITOR)
                .map(bp -> new PersonRoleDTO(bp.getPerson().getFirstName(),
                        bp.getPerson().getLastName(),
                        bp.getRole().name()))
                .toList();
    }

    private List<PersonRoleDTO> getPeopleByRole(Book book, Role role) {
        return book.getBookPeopleRoles().stream()
                .filter(bp -> bp.getRole().equals(role))
                .map(bp -> new PersonRoleDTO(bp.getPerson().getFirstName(),
                        bp.getPerson().getLastName(), role.name()))
                .toList();
    }
}
