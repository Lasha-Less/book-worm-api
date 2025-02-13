package com.lb.book_worm_api.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference // Prevents recursion when serializing BookPeopleRole -> Book
    private Book book;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @JsonBackReference // prevents recursion when serializing BookPeopleRole -> Person
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
