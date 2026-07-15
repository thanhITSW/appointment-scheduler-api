package com.appointment.service.impl;

import com.appointment.config.ApplicationProperties;
import com.appointment.service.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CookieServiceImpl implements CookieService {
    private final ApplicationProperties applicationProperties;

    @Override
    public String generateSessionCookie(String cookieName, String cookieValue) {
        ApplicationProperties.CookieConfig cookie = applicationProperties.getCookie();
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie
                .from(cookieName, cookieValue)
                .path(cookie.getAttributes().getPath())
                .sameSite(cookie.getAttributes().getSameSite())
                .httpOnly(cookie.getAttributes().isHttpOnly())
                .secure(cookie.getAttributes().isSecure());

        // Only set domain if explicitly configured
        String domain = cookie.getDomains();
        if (domain != null && !domain.isBlank()) {
            builder.domain(domain);
        }

        return builder.build().toString();
    }

    @Override
    public Optional<Cookie> getCookieByName(HttpServletRequest request, String cookieName) {
        return Optional.ofNullable(WebUtils.getCookie(request, cookieName));
    }
}
