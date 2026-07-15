package com.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccessDeniedException extends ResponseStatusException {

    private static final long serialVersionUID = 25745116449339264L;
    private final String messageCode;

    public AccessDeniedException(String messageCode) {
        super(HttpStatus.FORBIDDEN, messageCode);
        this.messageCode = messageCode;
    }

    public AccessDeniedException(String messageCode, Throwable cause) {
        super(HttpStatus.FORBIDDEN, messageCode, cause);
        this.messageCode = messageCode;
    }

    @Override
    public String getMessage() {
        return messageCode;
    }
}
