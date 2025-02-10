package com.lb.book_worm_api.dto;

import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.validation.AtLeastOneCreatorRequired;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AtLeastOneCreatorRequired
public class BookInputDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private List<Person> authors; //can be null if editors is not null
    private List<Person> editors; //can be null if authors is not null
    private List<Person> translators; //optional
    private List<Person> contributors; //optional
    private List<Person> illustrators; //optional
    private List<Person> others; //optional

    @NotBlank(message = "Language is required")
    private final String language; // if user enters 'english', backend will translate to 'en'

    private String originalLanguage; // same as above applies

    @Min(value = 1000, message = "Publication year must be a valid year")
    @Max(value = 2100, message = "Publication year must be a reasonable future year")
    private Integer publicationYear;

    @Min(value = -5000, message = "Historical date must be between 5000 BCE and 2100 CE")
    @Max(value = 2100, message = "Historical date must be between 5000 BCE and 2100 CE")
    private Integer historicalDate;
    private String publisher;

    @NotBlank(message = "Format is required")
    private final String format;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Stock status must be specified")
    private Boolean inStock;

    @NotEmpty(message = "At least one collection must be specified")
    private List<String> collections; // Collection names only.

    public BookInputDTO(
            String title,
            List<Person> authors,
            List<Person> editors,
            String language,
            String format,
            String location,
            Boolean inStock,
            List<String> collections) {
        this.title = title;
        this.authors = authors;
        this.editors = editors;
        this.language = language;
        this.format = format;
        this.location = location;
        this.inStock = inStock;
        this.collections = collections;
    }

    public BookInputDTO(
            String title,
            List<Person> authors,
            List<Person> editors,
            List<Person> translators,
            List<Person> contributors,
            List<Person> illustrators,
            List<Person> others,
            String language,
            String originalLanguage,
            Integer publicationYear,
            Integer historicalDate,
            String publisher,
            String format,
            String location,
            Boolean inStock,
            List<String> collections) {
        this.title = title;
        this.authors = authors;
        this.editors = editors;
        this.translators = translators;
        this.contributors = contributors;
        this.illustrators = illustrators;
        this.others = others;
        this.language = language;
        this.originalLanguage = originalLanguage;
        this.publicationYear = publicationYear;
        this.historicalDate = historicalDate;
        this.publisher = publisher;
        this.format = format;
        this.location = location;
        this.inStock = inStock;
        this.collections = collections;
    }

    public void setTranslators(List<Person> translators) {
        this.translators = translators;
    }

    public void setContributors(List<Person> contributors) {
        this.contributors = contributors;
    }

    public void setIllustrators(List<Person> illustrators) {
        this.illustrators = illustrators;
    }

    public void setOthers(List<Person> others) {
        this.others = others;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setHistoricalDate(Integer historicalDate) {
        this.historicalDate = historicalDate;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setTitle(String additionalText) {
        if (additionalText != null && !additionalText.trim().isEmpty()) {
            if (this.title == null) {
                this.title = additionalText.trim(); // If title was null, initialize it
            } else {
                this.title = this.title + ": " + additionalText.trim();
            }
        }
    }

    public void setAuthors(Person author) {
        if (author != null){
            if (this.authors == null) {
                this.authors = new ArrayList<>();
            }
            if (!this.authors.contains(author)){
                this.authors.add(author);
            }
        }
    }

    public void setEditors(Person editor) {
        if (editor != null){
            if (this.editors == null){
                this.editors = new ArrayList<>();
            }
        }
        if (!this.editors.contains(editor)){
            this.editors.add(editor);
        }
    }

    public void setLocation(String location) {
        if (location !=null && !location.trim().isEmpty()){
            this.location = location.trim();
        }
    }

    public void setInStock(Boolean inStock) {
        if (inStock != null){
            this.inStock = inStock;
        }
    }

    public void setCollections(List<String> collections) {
        if (collections != null){
            this.collections = new ArrayList<>(collections);
        }
    }

}
