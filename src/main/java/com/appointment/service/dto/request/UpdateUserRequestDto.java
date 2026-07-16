package com.appointment.service.dto.request;

import com.appointment.constant.ErrorCodeConstant;
import com.appointment.validation.anotation.Password;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateUserRequestDto {

    @NotNull(message = ErrorCodeConstant.ERR_EMPLOYEE_ID_REQUIRED)
    private String employeeId;

    @Password
    private String password;
}
