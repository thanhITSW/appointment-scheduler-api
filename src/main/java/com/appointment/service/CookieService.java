package com.appointment.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public interface CookieService {

    String generateSessionCookie(String cookieName, String cookieValue);

    Optional<Cookie> getCookieByName(HttpServletRequest request, String cookieName);
}
