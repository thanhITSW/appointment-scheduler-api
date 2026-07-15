package com.appointment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedException extends ResponseStatusException {

    private static final long serialVersionUID = -6810134007413964408L;
    private final String messageCode;

    public UnauthorizedException(String messageCode) {
        super(HttpStatus.UNAUTHORIZED, messageCode);
        this.messageCode = messageCode;
    }

    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, "unauthorized");
        this.messageCode = "unauthorized";
    }

    public UnauthorizedException(String messageCode, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, messageCode, cause);
        this.messageCode = messageCode;
    }

    @Override
    public String getMessage() {
        return messageCode;
    }
}
