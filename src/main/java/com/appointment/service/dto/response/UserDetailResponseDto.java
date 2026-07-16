package com.appointment.service.dto.response;

import com.appointment.enumeration.UserRole;
import com.appointment.enumeration.UserStatus;
import lombok.Data;

@Data
public class UserDetailResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private UserRole role;
    private UserStatus status;
}
