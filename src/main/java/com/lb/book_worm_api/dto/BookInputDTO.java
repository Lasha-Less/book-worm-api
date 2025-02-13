package com.lb.book_worm_api.dto;

import com.lb.book_worm_api.exception.ValidationException;
import com.lb.book_worm_api.model.Person;
import com.lb.book_worm_api.validation.AtLeastOneCreatorRequired;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BookInputDTO {

    // Mandatory attributes

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "At least one author or editor is required")
    private List<PersonRoleInputDTO> authors;

    @NotNull(message = "At least one author or editor is required")
    private List<PersonRoleInputDTO> editors;

    @NotBlank(message = "Language is required")
    private final String language; // if user enters 'english', backend will translate to 'en'

    @NotBlank(message = "Format is required")
    private final String format;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Stock status must be specified")
    private Boolean inStock;

    @NotEmpty(message = "At least one collection must be specified")
    private List<String> collections; // Collection names only.

    // Optional attributes

    private String originalLanguage; // same as above applies

    @Min(value = 1000, message = "Publication year must be a valid year")
    @Max(value = 2100, message = "Publication year must be a reasonable future year")
    private Integer publicationYear;

    @Min(value = -5000, message = "Historical date must be between 5000 BCE and 2100 CE")
    @Max(value = 2100, message = "Historical date must be between 5000 BCE and 2100 CE")
    private Integer historicalDate;

    private String publisher;

    private List<PersonRoleInputDTO> others = new ArrayList<>(); // Optional field, initialized as an empty list

    public BookInputDTO(
            String title,
            List<PersonRoleInputDTO> authors,
            List<PersonRoleInputDTO> editors,
            String language,
            String format,
            String location,
            Boolean inStock,
            List<String> collections) {
        this.title = title;
        this.authors = authors != null ? authors : new ArrayList<>();
        this.editors = editors != null ? editors : new ArrayList<>();
        this.language = language;
        this.format = format;
        this.location = location;
        this.inStock = inStock;
        this.collections = collections;

        // Ensure at least one author or editor exists
        if (this.authors.isEmpty() && this.editors.isEmpty()) {
            throw new ValidationException("A book must have at least one author or editor.");
        }
    }
}
