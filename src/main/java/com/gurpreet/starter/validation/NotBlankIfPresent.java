package com.gurpreet.starter.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * For optional PATCH-style fields: allows null (field not being updated)
 * but rejects an empty/blank string if the field IS supplied.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotBlankIfPresent.Validator.class)
@Documented
public @interface NotBlankIfPresent {

    String message() default "must not be blank when provided";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<NotBlankIfPresent, String> {
        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value == null || !value.trim().isEmpty();
        }
    }
}
