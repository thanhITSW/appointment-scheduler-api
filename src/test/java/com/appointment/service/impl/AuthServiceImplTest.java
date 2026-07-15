package com.appointment.service.impl;

import com.appointment.constant.ErrorCodeConstant;
import com.appointment.enumeration.TokenValidationType;
import com.appointment.exception.BadRequestException;
import com.appointment.security.dto.JwtTokenDto;
import com.appointment.security.jwt.JwtTokenPayload;
import com.appointment.security.jwt.JwtTokenProvider;
import com.appointment.security.model.CustomUserDetail;
import com.appointment.service.LoginSessionHandlerService;
import com.appointment.service.dto.request.AuthenticationRequestDto;
import com.appointment.service.dto.request.LoginSessionRequestDto;
import com.appointment.service.dto.request.RefreshTokenRequestDto;
import com.appointment.service.dto.response.LoginResponseDto;
import com.appointment.service.dto.response.RefreshTokenResponse;
import com.appointment.service.mapper.LoginSessionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Tests")
class AuthServiceImplTest {

    @Mock
    private LoginSessionHandlerService loginSessionHandlerService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private LoginSessionMapper loginSessionMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private AuthenticationRequestDto testAuthRequest;
    private RefreshTokenRequestDto testRefreshRequest;
    private CustomUserDetail testUserDetail;
    private JwtTokenDto testJwtToken;
    private JwtTokenPayload testTokenPayload;
    private Authentication testAuthentication;
    private LoginSessionRequestDto testLoginSessionRequest;

    @BeforeEach
    void setUp() {
        testAuthRequest = new AuthenticationRequestDto();
        testAuthRequest.setEmployeeId("innox1");
        testAuthRequest.setPassword("password");

        testRefreshRequest = new RefreshTokenRequestDto();
        testRefreshRequest.setRefreshToken("refresh-token");

        testUserDetail = CustomUserDetail.builder()
                .username("innox1")
                .password("encoded")
                .build();

        testJwtToken = JwtTokenDto.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .sessionId("session-1")
                .username("innox1")
                .expiredTime(Instant.now().plusSeconds(3600))
                .build();

        testTokenPayload = JwtTokenPayload.builder()
                .username("innox1")
                .isRefreshToken(true)
                .build();

        testAuthentication = mock(Authentication.class);
        testLoginSessionRequest = new LoginSessionRequestDto();
    }

    @Test
    @DisplayName("authenticate should return tokens on success")
    void authenticate_success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(testAuthentication);
        when(testAuthentication.getPrincipal()).thenReturn(testUserDetail);
        when(jwtTokenProvider.generateLoginToken(testUserDetail)).thenReturn(testJwtToken);
        when(loginSessionMapper.toLoginSessionRequest(testJwtToken)).thenReturn(testLoginSessionRequest);
        when(loginSessionMapper.toLoginSessionRequest(testLoginSessionRequest, testAuthRequest))
                .thenReturn(testLoginSessionRequest);

        LoginResponseDto response = authService.authenticate(testAuthRequest);

        assertThat(response.isAuthenticated()).isTrue();
        assertThat(response.getJwtTokenDto()).isEqualTo(testJwtToken);
        verify(loginSessionHandlerService).saveLoginSession(testLoginSessionRequest);
    }

    @Test
    @DisplayName("authenticate should wrap auth failures as BadRequestException")
    void authenticate_failure() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException(ErrorCodeConstant.ERR_AUTHENTICATION_FAILED));

        assertThatThrownBy(() -> authService.authenticate(testAuthRequest))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("refreshToken should return new tokens")
    void refreshToken_success() {
        when(jwtTokenProvider.validateToken("refresh-token")).thenReturn(TokenValidationType.VALID);
        when(jwtTokenProvider.parseJwtToken("refresh-token")).thenReturn(testTokenPayload);
        when(jwtTokenProvider.generateFromToken(testTokenPayload)).thenReturn(testJwtToken);
        when(loginSessionMapper.toLoginSessionRequest(testJwtToken)).thenReturn(testLoginSessionRequest);

        RefreshTokenResponse response = authService.refreshToken(testRefreshRequest);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        verify(loginSessionHandlerService).renewExpiredSession(testLoginSessionRequest);
    }

    @Test
    @DisplayName("refreshToken should reject expired refresh token")
    void refreshToken_expired() {
        when(jwtTokenProvider.validateToken("refresh-token")).thenReturn(TokenValidationType.EXPIRED);

        assertThatThrownBy(() -> authService.refreshToken(testRefreshRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(ErrorCodeConstant.REFRESH_TOKEN_EXPIRED);
    }
}
