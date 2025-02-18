package com.lb.book_worm_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BookDTO {

    private Long id;
    private String title;
    private String lingo;
    private String format;
    private boolean inStock;
    private String location;
    private Integer publicationYear;
    private Integer historicalDate;
    private String originalLanguage;
    private String publisher;

    // Separate lists for different roles
    private List<PersonRoleDTO> authors;
    private List<PersonRoleDTO> editors;
    private List<PersonRoleDTO> others;

    // Only collection names
    private List<String> collections;
}
