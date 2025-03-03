package com.lb.book_worm_api.validation;

import com.lb.book_worm_api.exception.ValidationException;
import org.springframework.stereotype.Service;

@Service
public class PersonValidator {

    public void validatePersonInput(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new ValidationException("Last name is required.");
        }
    }
}
