package com.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DataConflictException extends ResponseStatusException {

    private static final long serialVersionUID = 2574553436449339264L;
    private final String messageCode;

    public DataConflictException(String messageCode) {
        super(HttpStatus.CONFLICT, messageCode);
        this.messageCode = messageCode;
    }

    public DataConflictException(String messageCode, Throwable cause) {
        super(HttpStatus.CONFLICT, messageCode, cause);
        this.messageCode = messageCode;
    }

    @Override
    public String getMessage() {
        return messageCode;
    }
}
