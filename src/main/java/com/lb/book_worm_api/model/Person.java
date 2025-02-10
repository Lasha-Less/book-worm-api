package com.lb.book_worm_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @Column(name = "prefix", length = 10)
    private String prefix;

    @Column(name = "last_name", length = 100)
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookPeopleRole> bookPeopleRoles = new HashSet<>();

    public Person() {
    }

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }



    public Person(String firstName, String prefix, String lastName) {
        this.firstName = firstName;
        this.prefix = prefix;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + firstName + '\'' +
                '}';
    }

}
