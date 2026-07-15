package com.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InternalProcessException extends ResponseStatusException {

    private static final long serialVersionUID = 5017035103999685011L;
    private final String messageCode;

    public InternalProcessException(String messageCode) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, messageCode);
        this.messageCode = messageCode;
    }

    public InternalProcessException(String messageCode, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, messageCode, cause);
        this.messageCode = messageCode;
    }

    @Override
    public String getMessage() {
        return messageCode;
    }
}
