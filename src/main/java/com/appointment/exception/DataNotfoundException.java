package com.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DataNotfoundException extends ResponseStatusException {

    private static final long serialVersionUID = 2574553436449339264L;
    private final String messageCode;

    public DataNotfoundException(String messageCode) {
        super(HttpStatus.NOT_FOUND, messageCode);
        this.messageCode = messageCode;
    }

    public DataNotfoundException(String messageCode, Throwable cause) {
        super(HttpStatus.NOT_FOUND, messageCode, cause);
        this.messageCode = messageCode;
    }

    @Override
    public String getMessage() {
        return messageCode;
    }
}
