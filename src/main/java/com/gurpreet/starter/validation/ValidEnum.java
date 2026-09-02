package com.gurpreet.starter.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Usage:
 *   @ValidEnum(enumClass = LeadStatus.class, message = "Invalid lead status")
 *   private String status;
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEnumValidator.class)
@Documented
public @interface ValidEnum {

    Class<? extends Enum<?>> enumClass();

    String message() default "must be any of the allowed enum values";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
