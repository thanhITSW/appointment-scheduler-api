package com.appointment.service.dto.request;

import com.appointment.enumeration.UserRole;
import com.appointment.validation.anotation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.appointment.constant.ErrorCodeConstant.ERR_EMPLOYEE_ID_REQUIRED;
import static com.appointment.constant.ErrorCodeConstant.ERR_FULL_NAME_REQUIRED;
import static com.appointment.constant.ErrorCodeConstant.ERR_USER_EMAIL_INVALID;
import static com.appointment.constant.ErrorCodeConstant.ERR_USER_EMAIL_REQUIRED;
import static com.appointment.constant.ErrorCodeConstant.ERR_USER_ROLE_REQUIRED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDto {

    @NotBlank(message = ERR_EMPLOYEE_ID_REQUIRED)
    private String employeeId;

    @NotBlank(message = ERR_USER_EMAIL_REQUIRED)
    @Email(message = ERR_USER_EMAIL_INVALID)
    private String email;

    @NotBlank(message = ERR_FULL_NAME_REQUIRED)
    private String fullName;

    @Password
    private String password;

    @NotNull(message = ERR_USER_ROLE_REQUIRED)
    private UserRole role;
}
