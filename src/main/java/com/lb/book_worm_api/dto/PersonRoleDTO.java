package com.lb.book_worm_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PersonRoleDTO {

    private String firstName;
    private String lastName;
    private String role;
}
