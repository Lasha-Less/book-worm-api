package com.lb.book_worm_api.util;

import com.lb.book_worm_api.dto.PersonDTO;
import com.lb.book_worm_api.dto.PersonRoleInputDTO;
import com.lb.book_worm_api.model.Book;
import com.lb.book_worm_api.model.BookPeopleRole;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.model.Role;
import org.springframework.stereotype.Component;

@Component
public class BookPeopleRoleMapper {

    /**
     * Converts a PersonDTO to a Person entity.
     * @param personDTO The DTO containing person details.
     * @return A Person entity.
     */
    public Person toPersonEntity(PersonDTO personDTO) {
        return new Person(personDTO.getFirstName(), personDTO.getPrefix(), personDTO.getLastName());
    }

    /**
     * Converts a PersonRoleInputDTO to a Person entity.
     * @param inputDTO The DTO containing person-role details.
     * @return A Person entity.
     */
    public Person toPersonEntity(PersonRoleInputDTO inputDTO) {
        return new Person(inputDTO.getFirstName(), inputDTO.getPrefix(), inputDTO.getLastName());
    }

    /**
     * Creates a BookPeopleRole entity from a Book, Person, and Role.
     * @param book The book entity.
     * @param person The person entity.
     * @param role The role to be assigned.
     * @return A new BookPeopleRole entity.
     */
    public BookPeopleRole toBookPeopleRoleEntity(Book book, Person person, Role role) {
        return new BookPeopleRole(book, person, role);
    }
}
