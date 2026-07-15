package com.appointment.service.dto.request;

import com.appointment.enumeration.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.appointment.constant.ErrorCodeConstant.ERR_USER_ID_REQUIRED;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditUserRequestDto {

    @NotNull(message = ERR_USER_ID_REQUIRED)
    private Long id;

    private UserRole role;

    private String password;

    private String note;
}
