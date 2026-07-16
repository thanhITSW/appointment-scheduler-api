package com.appointment.validation;

import com.appointment.validation.anotation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_COMPLEXITY;
import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_LENGTH;
import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_MISSING_LOWERCASE;
import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_MISSING_NUMBER;
import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_MISSING_SPECIAL;
import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_MISSING_UPPERCASE;
import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_REQUIRED;

public class PasswordValidation
        implements ConstraintValidator<Password, String>
{
    private int minLength;
    private int maxLength;
    @Override
    public void initialize(Password password)
    {
        ConstraintValidator.super.initialize(password);
        this.minLength = password.minLength();
        this.maxLength = password.maxLength();
    }

    @Override
    public boolean isValid(
            String passwordInput,
            ConstraintValidatorContext context)
    {
        if (passwordInput == null)
        {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(ERR_PASSWORD_REQUIRED)
                    .addConstraintViolation();
            return false;
        }

        // Check minimum length if specified
        if (passwordInput.length() < minLength)
        {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(ERR_PASSWORD_LENGTH)
                    .addConstraintViolation();
            return false;
        }

        // Check maximum length if specified
        if (maxLength > 0 && passwordInput.length() > maxLength)
        {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(ERR_PASSWORD_LENGTH)
                    .addConstraintViolation();
            return false;
        }

        // Check for uppercase letters
        if (!passwordInput.matches(".*[A-Z].*"))
        {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(ERR_PASSWORD_MISSING_UPPERCASE)
                    .addConstraintViolation();
            return false;
        }

        // Check for lowercase letters
        if (!passwordInput.matches(".*[a-z].*"))
        {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(ERR_PASSWORD_MISSING_LOWERCASE)
                    .addConstraintViolation();
            return false;
        }

        // Check for numbers
        if (!passwordInput.matches(".*\\d.*"))
        {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(ERR_PASSWORD_MISSING_NUMBER)
                    .addConstraintViolation();
            return false;
        }

        // Check for special characters
        if (!passwordInput.matches(".*[\\W_].*"))
        {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(ERR_PASSWORD_MISSING_SPECIAL)
                    .addConstraintViolation();
            return false;
        }

        // Verify all 4 character types are present (complexity check)
        int complexityCount = 0;
        if (passwordInput.matches(".*[A-Z].*")) complexityCount++;
        if (passwordInput.matches(".*[a-z].*")) complexityCount++;
        if (passwordInput.matches(".*\\d.*")) complexityCount++;
        if (passwordInput.matches(".*[\\W_].*")) complexityCount++;

        if (complexityCount < 4)
        {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(ERR_PASSWORD_COMPLEXITY)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
