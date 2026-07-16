package com.appointment.utils;

import com.appointment.exception.UnauthorizedException;
import com.appointment.security.jwt.JwtTokenPayload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public final class SecurityUtils
{
    private SecurityUtils() {}

    public static Optional<JwtTokenPayload> getCurrentUserLogin()
    {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractCredentials(securityContext.getAuthentication()));
    }

    public static Optional<String> getStrCurrentUserLogin()
    {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    public static JwtTokenPayload getCurrentUserLoginOrThrow()
    {
        return getCurrentUserLogin().orElseThrow(UnauthorizedException ::new);
    }

    public static Long getCurrentUserIdOrThrow()
    {
        return getCurrentUserLogin()
                .orElseThrow(UnauthorizedException::new)
                .getUserId();
    }

    private static String extractPrincipal(Authentication authentication)
    {
        if (Objects.isNull(authentication))
        {
            return null;
        }

        if (authentication.getPrincipal() instanceof String principal)
        {
            return principal;
        }

        return null;
    }

    public static JwtTokenPayload extractCredentials(Authentication authentication)
    {
        if (Objects.isNull(authentication) || Objects.isNull(authentication.getPrincipal()))
        {
            return null;
        }

        if (authentication.getPrincipal() instanceof JwtTokenPayload currentUserLogin)
        {
            return currentUserLogin;
        }

        return null;
    }

}
