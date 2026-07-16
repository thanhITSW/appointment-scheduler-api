package com.appointment.security.handler;

import com.appointment.service.LoginSessionHandlerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogoutHandlerEvent implements LogoutHandler {
    private final LoginSessionHandlerService loginSessionHandlerService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextHandler.extractPrinciple().ifPresent(
                session -> loginSessionHandlerService.inactiveUserSession(session.getUserId(), session.getTokenId()));
    }
}
