package com.lb.book_worm_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AtLeastOneCreatorValidator.class)
@Target({ElementType.TYPE}) // Applies to the entire class, not just fields
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneCreatorRequired {
    String message() default "A book must have at least one author or editor";
    Class<? extends Payload>[] payload() default {};
}
