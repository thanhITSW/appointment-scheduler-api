package com.appointment.service;

import com.appointment.security.jwt.JwtTokenPayload;
import com.appointment.service.dto.request.LoginSessionRequestDto;

import java.util.Optional;


public interface LoginSessionHandlerService {
    void saveLoginSession(LoginSessionRequestDto request);

    void inactiveUserSession(Long userId, String sessionId);

    Optional<JwtTokenPayload> extractCacheLoginSession(String sessionId);

    void renewExpiredSession(LoginSessionRequestDto sessionDto);
}
