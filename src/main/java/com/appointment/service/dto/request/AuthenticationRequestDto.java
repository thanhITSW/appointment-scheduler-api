package com.appointment.service.dto.request;

import com.appointment.constant.ErrorCodeConstant;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthenticationRequestDto {

    @NotNull(message = ErrorCodeConstant.REQUIRED_VALIDATE)
    private String employeeId;

    @NotNull(message = ErrorCodeConstant.REQUIRED_VALIDATE)
    private String password;

    private String ipAddress;
    private String userAgent;
    private String location;
}
