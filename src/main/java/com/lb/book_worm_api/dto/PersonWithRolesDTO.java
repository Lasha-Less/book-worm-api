package com.lb.book_worm_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PersonWithRolesDTO {
    private Long id;
    private String firstName;
    private String prefix;
    private String lastName;
    private List<String> roles; // ✅ Stores roles instead of books
}
