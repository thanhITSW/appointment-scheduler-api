package com.appointment.service.dto.request;

import com.appointment.validation.anotation.Password;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_REQUIRED;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {

    @NotBlank(message = ERR_PASSWORD_REQUIRED)
    private String currentPassword;

    @NotBlank(message = ERR_PASSWORD_REQUIRED)
    @Password
    private String newPassword;

    @NotBlank(message = ERR_PASSWORD_REQUIRED)
    private String confirmNewPassword;
}
