package com.appointment.security.handler;

import com.appointment.security.jwt.JwtTokenPayload;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)

public class SecurityContextHandler {
    public static Optional<Long> extractUserId() {
        return extractPrinciple().map(JwtTokenPayload::getUserId);
    }

    public static Optional<String> extractEmail() {
        return extractPrinciple().map(JwtTokenPayload::getUsername);
    }

    public static Optional<JwtTokenPayload> extractPrinciple() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .filter(JwtTokenPayload.class::isInstance)
                .map(JwtTokenPayload.class::cast);
    }
}
