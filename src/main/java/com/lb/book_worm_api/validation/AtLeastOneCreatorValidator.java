package com.lb.book_worm_api.validation;

import com.lb.book_worm_api.dto.BookInputDTO;
import com.lb.book_worm_api.dto.PersonRoleInputDTO;
import com.lb.book_worm_api.model.Person;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class AtLeastOneCreatorValidator implements ConstraintValidator<AtLeastOneCreatorRequired, BookInputDTO> {

    @Override
    public boolean isValid(BookInputDTO dto, ConstraintValidatorContext context) {

        @NotNull(message =
                "At least one author or editor is required") List<PersonRoleInputDTO> authors = dto.getAuthors();
        @NotNull(message =
                "At least one author or editor is required") List<PersonRoleInputDTO> editors = dto.getEditors();

        return (authors != null && !authors.isEmpty()) || (editors != null && !editors.isEmpty());
    }
}
