package com.appointment.service.dto.request;

import lombok.Data;

import java.time.Instant;

@Data
public class LoginSessionRequestDto {
    private Long userId;
    private String sessionId;
    private Instant expiresAt;
    private String ipAddress;
    private String userAgent;
    private String location;
    private TokenDetail tokenDetail;

    @Data
    public static class TokenDetail {
        private String token;
        private String refreshToken;
    }
}
