package com.appointment.controller.errorhandler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.appointment.constant.ErrorCodeConstant;
import com.appointment.exception.AccessDeniedException;
import com.appointment.exception.BadRequestException;
import com.appointment.exception.DataConflictException;
import com.appointment.exception.DataNotfoundException;
import com.appointment.exception.UnauthorizedException;
import com.appointment.service.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        return createErrorResponse(ex.getMessage(), ErrorCodeConstant.INVALID_VALIDATE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataNotfoundException.class)
    public ResponseEntity<ErrorResponse> handleDataNotFoundException(DataNotfoundException ex) {
        return createErrorResponse(ex.getMessage(), ErrorCodeConstant.ERROR_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataConflictException.class)
    public ResponseEntity<ErrorResponse> handleDataConflictException(DataConflictException ex) {
        return createErrorResponse(ex.getMessage(), ErrorCodeConstant.ERROR_CONFLICT, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        return createErrorResponse(ex.getMessage(), ErrorCodeConstant.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return createErrorResponse(ex.getMessage(), ErrorCodeConstant.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String firstError = errors.isEmpty() ? "Validation failed" : errors.values().iterator().next();
        return createErrorResponse(firstError, ErrorCodeConstant.INVALID_VALIDATE, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnrecognizedPropertyException.class})
    public ResponseEntity<ErrorResponse> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex) {
        String errorMessage = String.format("Unknown field '%s' is not allowed", ex.getPropertyName());
        return createErrorResponse(errorMessage, ErrorCodeConstant.ERR_UNKNOWN_FIELD, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(JsonParseException ex) {
        return createErrorResponse("Invalid JSON format", ErrorCodeConstant.ERR_INVALID_JSON, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

        // Handle specific nested exceptions without recursion
        Throwable rootCause = getRootCause(ex);

        if (rootCause instanceof UnrecognizedPropertyException unrecognizedEx) {
            String errorMessage = String.format("Unknown field '%s' is not allowed", unrecognizedEx.getPropertyName());
            return createErrorResponse(errorMessage, ErrorCodeConstant.ERR_UNKNOWN_FIELD, HttpStatus.BAD_REQUEST);
        }

        if (rootCause instanceof JsonParseException) {
            return createErrorResponse("Invalid JSON format", ErrorCodeConstant.ERR_INVALID_JSON, HttpStatus.BAD_REQUEST);
        }

        return createErrorResponse("Internal server error", ErrorCodeConstant.INTERNAL_SERVER, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method to create consistent error responses
    private ResponseEntity<ErrorResponse> createErrorResponse(String message, String errorCode, HttpStatus status) {
        ErrorResponse errorResponse = ErrorResponse.of(message, errorCode);
        return ResponseEntity.status(status).body(errorResponse);
    }

    // Helper method to get root cause without recursion issues
    private Throwable getRootCause(Throwable throwable) {
        Throwable rootCause = throwable;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause;
    }
}
