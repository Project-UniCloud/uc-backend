package com.unicloudapp.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StudentLoginValidator.class)
public @interface StudentLogin {
    String message() default "must start with 's' followed by digits";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
