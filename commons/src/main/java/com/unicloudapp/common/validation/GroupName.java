package com.unicloudapp.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;

@Documented
@NotBlank
@Size(min = 1, max = 128)
@Pattern(regexp = "^[\\wąćęłńóśźżĄĆĘŁŃÓŚŹŻ\\- ]+$")
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface GroupName {
    String message() default "test";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}