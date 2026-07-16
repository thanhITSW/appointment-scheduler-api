package com.appointment.security.filter;

import com.appointment.enumeration.ClientType;
import com.appointment.enumeration.TokenValidationType;
import com.appointment.security.jwt.JwtTokenPayload;
import com.appointment.security.jwt.JwtTokenProvider;
import com.appointment.service.dto.request.LoginSessionRequestDto;
import com.appointment.service.dto.response.ErrorResponse;
import com.appointment.utils.JsonUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip authentication for public endpoints
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        ClientType clientType = tokenProvider.getClientType(request);
        Optional<JwtTokenPayload> tokenDetail = tokenProvider.getTokenDetail(request, clientType);

        if (tokenDetail.isEmpty()) {
            log.info("Token not exist from cookie");
            handleSessionExpire(response);
            return;
        }

        setAuthentication(tokenDetail.get());

        filterChain.doFilter(request, response);
    }

    private void handleSessionExpire(HttpServletResponse response) throws IOException {
        final String errorCode = "error.session.expired";
        doResponse(response,  ErrorResponse.of(errorCode, "Session expired"));
    }


    private static void doResponse(HttpServletResponse response, ErrorResponse problem) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(JsonUtil.writeObjectToJson(problem));
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/api/v1/public/") ||
               requestURI.startsWith("/api/docs") ||
               requestURI.startsWith("/swagger") ||
               requestURI.startsWith("/v3/api-docs");
    }

    private void setAuthentication(JwtTokenPayload tokenPayload) {
        if (tokenPayload == null) {
            return;
        }
        Set<SimpleGrantedAuthority> authorities = Set.of(
                new SimpleGrantedAuthority(formatSecurityRole(tokenPayload.getRole()))
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                tokenPayload,
                null,
                authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String formatSecurityRole(String role) {
        return String.join("_", "ROLE", role);
    }

}
