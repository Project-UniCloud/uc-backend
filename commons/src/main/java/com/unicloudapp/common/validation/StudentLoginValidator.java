package com.unicloudapp.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StudentLoginValidator implements ConstraintValidator<StudentLogin, String> {

    @Override
    public boolean isValid(
            String s,
            ConstraintValidatorContext constraintValidatorContext
    ) {
        if (s == null) {
            return false;
        }
        return s.matches("s\\d+");
    }
}
