package com.appointment.service.dto.response;

import com.appointment.enumeration.UserRole;
import com.appointment.enumeration.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String fullName;
    private UserRole role;
    private UserStatus status;
    private Instant createdDate;
    private String createdBy;
    private String note;
    private String employeeId;

}
