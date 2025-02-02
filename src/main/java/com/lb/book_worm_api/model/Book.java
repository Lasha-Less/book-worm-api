package com.lb.book_worm_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name = "lingo", nullable = false, length = 2)
    private String lingo;

    @Column(nullable = false, length = 50)
    private String format;

    @Column(name= "in_stock", nullable = false)
    private boolean inStock;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "historical_date")
    private Integer historicalDate;

    @Column(name = "original_language", length = 50)
    private String originalLanguage;

    @Column(name = "publisher")
    private String publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookPeopleRole> bookPeopleRoles = new HashSet<>();

    @ManyToMany(mappedBy = "books", fetch = FetchType.LAZY)
    private Set<Collection>collections = new HashSet<>();

    public Book() {
    }

    public Book(String title, String language, String format, boolean inCollection, String location) {
        this.title = title;
        this.lingo = language;
        this.format = format;
        this.inStock = inCollection;
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
        this.lingo = language;
        this.format = format;
        this.inStock = inCollection;
        this.location = location;
        this.publicationYear = publicationYear;
        this.historicalDate = originalPublicationYear;
        this.originalLanguage = originalLanguage;
    }


    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", language='" + lingo + '\'' +
                ", format='" + format + '\'' +
                ", inCollection=" + inStock +
                ", location='" + location + '\'' +
                ", publicationYear=" + publicationYear +
                ", originalPublicationYear=" + historicalDate +
                ", originalLanguage='" + originalLanguage + '\'' +
                '}';
    }
}
