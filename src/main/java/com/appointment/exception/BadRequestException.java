package com.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BadRequestException extends ResponseStatusException {

    private static final long serialVersionUID = 2574553436449339264L;
    private final String messageCode;

    public BadRequestException(String messageCode) {
        super(HttpStatus.BAD_REQUEST, messageCode);
        this.messageCode = messageCode;
    }

    public BadRequestException(String messageCode, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, messageCode, cause);
        this.messageCode = messageCode;
    }

    @Override
    public String getMessage() {
        return messageCode;
    }
}
