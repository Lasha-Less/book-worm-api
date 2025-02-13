package com.lb.book_worm_api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PersonDTO {

    private Long id;
    private String firstName;
    private String lastName;

    // Only show Book title and their role(s)
    private List<BookRoleDTO> books;
}
