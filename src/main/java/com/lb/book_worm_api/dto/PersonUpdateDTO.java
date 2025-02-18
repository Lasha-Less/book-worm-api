package com.lb.book_worm_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PersonUpdateDTO {

    private String firstName;
    private String prefix;
    private String lastName;

}
