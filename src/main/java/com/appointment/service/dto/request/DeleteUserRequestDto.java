package com.appointment.service.dto.request;

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
public class DeleteUserRequestDto {

    @NotBlank(message = ERR_PASSWORD_REQUIRED)
    private String adminPassword;

    private String deleteReason;

}
