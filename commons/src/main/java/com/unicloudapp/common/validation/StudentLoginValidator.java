package com.unicloudapp.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

class StudentLoginValidator implements ConstraintValidator<StudentLogin, String> {

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
