package com.appointment.constant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageErrorConstant {
    public static final String NOT_FOUND = "{0} not found!";
    public static final String INTERNAL_SERVER_ERROR  = "An unexpected error occurred. Please try again later.";
    public static final String ALREADY_EXISTS = "{0} already exists!";
    public static final String ALREADY_DELETED = "{0} already deleted!";
    public static final String INVALID_FIELD = "Invalid {0}!";
    public static final String INVALID_VALIDATE_LENGTH = "Please enter between {0} and {1} characters!";
    public static final String INVALID_VALIDATE_REQUIRED = "Please enter a {0}!";
    public static final String INVALID_VALIDATE_BIGDECIMAL = "Please enter a valid number for {0}!";
    public static final String INVALID_VALIDATE_NUMBER_LESS_THAN_ZERO = "{0} must be greater than or equal to 0!";
    public static final String INVALID_VALIDATE_NUMBER = "Please enter a number";

    public static String getMessageError(String message, Object... args) {
        return MessageFormat.format(message, args);
    }
}
