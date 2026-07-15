package com.appointment.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String messageCode;
    private String message;
    
    public static ErrorResponse of(String messageCode, String message) {
        return new ErrorResponse(messageCode, message);
    }
}
