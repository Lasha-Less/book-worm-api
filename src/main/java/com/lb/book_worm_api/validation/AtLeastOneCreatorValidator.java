package com.lb.book_worm_api.validation;

import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.model.Person;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class AtLeastOneCreatorValidator implements ConstraintValidator<AtLeastOneCreatorRequired, BookInputDTO> {

    @Override
    public boolean isValid(BookInputDTO dto, ConstraintValidatorContext context) {

        List<Person> authors = dto.getAuthors();
        List<Person> editors = dto.getEditors();

        return (authors != null && !authors.isEmpty()) || (editors != null && !editors.isEmpty());
    }
}
