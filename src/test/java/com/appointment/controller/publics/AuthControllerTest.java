package com.appointment.controller.publics;

import com.appointment.security.dto.JwtTokenDto;
import com.appointment.service.AuthService;
import com.appointment.service.CookieService;
import com.appointment.service.dto.request.AuthenticationRequestDto;
import com.appointment.service.dto.request.RefreshTokenRequestDto;
import com.appointment.service.dto.response.LoginResponseDto;
import com.appointment.service.dto.response.RefreshTokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private CookieService cookieService;

    @InjectMocks
    private AuthController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /login returns tokens and session cookie")
    void login_success() throws Exception {
        AuthenticationRequestDto request = new AuthenticationRequestDto();
        request.setEmployeeId("admin01");
        request.setPassword("Admin@123");

        JwtTokenDto jwt = JwtTokenDto.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .sessionId("session-1")
                .build();

        when(authService.authenticate(any(AuthenticationRequestDto.class)))
                .thenReturn(LoginResponseDto.builder()
                        .isAuthenticated(true)
                        .jwtTokenDto(jwt)
                        .build());
        when(cookieService.generateSessionCookie(anyString(), eq("session-1")))
                .thenReturn("auth-session=session-1; Path=/; HttpOnly");

        mockMvc.perform(post("/api/v1/public/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("auth-session")))
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.jwtTokenDto.token").value("access-token"));

        verify(authService).authenticate(any(AuthenticationRequestDto.class));
    }

    @Test
    @DisplayName("POST /refresh returns new tokens")
    void refresh_success() throws Exception {
        RefreshTokenRequestDto request = new RefreshTokenRequestDto();
        request.setRefreshToken("refresh-token");

        when(authService.refreshToken(any(RefreshTokenRequestDto.class)))
                .thenReturn(RefreshTokenResponse.builder()
                        .accessToken("new-access")
                        .refreshToken("new-refresh")
                        .build());

        mockMvc.perform(post("/api/v1/public/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"));
    }
}
