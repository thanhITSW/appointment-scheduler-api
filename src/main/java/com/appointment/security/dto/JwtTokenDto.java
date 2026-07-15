package com.appointment.security.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class JwtTokenDto {
    private String token;
    private String refreshToken;
    private Instant expiredTime;
    private String username;
    private String employeeId;
    private Long userId;
    private String sessionId;
}
