package com.lb.book_worm_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookUpdateDTO {

    private String title;
    private List<PersonRoleInputDTO> authors;
    private List<PersonRoleInputDTO> editors;
    private String language;
    private String format;
    private String location;
    private Boolean inStock;
    private List<String> collections;
    private String originalLanguage;
    private Integer publicationYear;
    private Integer historicalDate;
    private String publisher;
    private List<PersonRoleInputDTO> others = new ArrayList<>();

}
