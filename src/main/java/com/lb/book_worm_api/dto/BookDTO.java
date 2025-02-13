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

    // Only include person names and their roles
    private List<PersonRoleDTO> people;

    // Only collection names
    private List<String> collections;
}
