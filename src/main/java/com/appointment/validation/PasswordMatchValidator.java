package com.appointment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        try {
            // Use reflection to get password and confirmPassword fields
            String password = (String) obj.getClass().getMethod("getPassword").invoke(obj);
            String confirmPassword = (String) obj.getClass().getMethod("getConfirmPassword").invoke(obj);

            if (password == null && confirmPassword == null) {
                return true;
            }

            if (password == null || confirmPassword == null) {
                return false;
            }

            return password.equals(confirmPassword);
        } catch (Exception e) {
            return false;
        }
    }
}
