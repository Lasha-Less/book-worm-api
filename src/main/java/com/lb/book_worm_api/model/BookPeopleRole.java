package com.lb.book_worm_api.model;
import com.lb.book_worm_api.model.Book;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "book_people_role",
        uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "person_id", "role"}))
public class BookPeopleRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public BookPeopleRole() {
    }

    public BookPeopleRole(Book book, Person person, Role role) {
        this.book = book;
        this.person = person;
        this.role = role;
    }

    @Override
    public String toString() {
        System.out.println("Book class: " + book.getClass().getName());
        return "BookPeopleRole{" +
                "id=" + id +
                ", bookID=" + (book != null ? book.getId() : "null") +
                ", person=" + (person != null ? person.getId() : "null") +
                ", role=" + role +
                '}';
    }
}
