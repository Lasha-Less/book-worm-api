package com.lb.book_worm_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(nullable = false, length = 50)
    private String format;

    @Column(nullable = false)
    private boolean inCollection;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "original_publication_year")
    private Integer originalPublicationYear;

    @Column(name = "original_language", length = 50)
    private String originalLanguage;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookPeopleRole> bookPeopleRoles = new ArrayList<>();

    public Book() {
    }

    public Book(String title, String language, String format, boolean inCollection, String location) {
        this.title = title;
        this.language = language;
        this.format = format;
        this.inCollection = inCollection;
        this.location = location;
    }

    public Book(String title,
                String language,
                String format,
                boolean inCollection,
                String location,
                Integer publicationYear,
                Integer originalPublicationYear,
                String originalLanguage) {
        this.title = title;
        this.language = language;
        this.format = format;
        this.inCollection = inCollection;
        this.location = location;
        this.publicationYear = publicationYear;
        this.originalPublicationYear = originalPublicationYear;
        this.originalLanguage = originalLanguage;
    }


    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", format='" + format + '\'' +
                ", inCollection=" + inCollection +
                ", location='" + location + '\'' +
                ", publicationYear=" + publicationYear +
                ", originalPublicationYear=" + originalPublicationYear +
                ", originalLanguage='" + originalLanguage + '\'' +
                '}';
    }
}
