package com.lb.book_worm_api.validation;

import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import com.lb.book_worm_api.repository.BookPeopleRoleRepo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class BookPeopleRoleValidator {

    private final BookPeopleRoleRepo bookPeopleRoleRepo;

    public BookPeopleRoleValidator(BookPeopleRoleRepo bookPeopleRoleRepo) {
        this.bookPeopleRoleRepo = bookPeopleRoleRepo;
    }

    /**
     * Validates whether a role assignment is valid before persisting.
     *
     * @param book   The book entity.
     * @param person The person entity.
     * @param role   The role to be assigned.
     */
    public void validateRoleAssignment(Book book, Person person, Role role) {
        if (bookPeopleRoleRepo.findByBookAndPersonAndRole(book, person, role).isPresent()) {
            throw new ValidationException("Person " + person.getFirstName() + " is already assigned as " + role +
                    " for book " + book.getTitle());
        }
    }

    /**
     * Ensures that a book has at least one author or editor after a role update/removal.
     *
     * @param book The book entity.
     * @throws ValidationException if removing the role would leave the book without required authors or editors.
     */
    public void validateAtLeastOneAuthorOrEditor(Book book) {
        Set<BookPeopleRole> roles = book.getBookPeopleRoles();
        long remainingAuthors = roles.stream().filter(r -> r.getRole() == Role.AUTHOR).count();
        long remainingEditors = roles.stream().filter(r -> r.getRole() == Role.EDITOR).count();

        if (remainingAuthors == 0) {
            throw new ValidationException("A book must have at least one author.");
        }
        if (remainingEditors == 0) {
            throw new ValidationException("A book must have at least one editor.");
        }
    }

    /**
     * Validates if the given role exists and is valid.
     *
     * @param roleStr The role string from input.
     * @return The validated Role enum.
     */
    public Role validateAndConvertRole(String roleStr) {
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid role provided: " + roleStr);
        }
    }
}
