package com.appointment.service.impl;

import com.appointment.constant.ErrorCodeConstant;
import com.appointment.enumeration.TokenValidationType;
import com.appointment.exception.BadRequestException;
import com.appointment.exception.UnauthorizedException;
import com.appointment.security.dto.JwtTokenDto;
import com.appointment.security.jwt.JwtTokenProvider;
import com.appointment.security.model.CustomUserDetail;
import com.appointment.service.AuthService;
import com.appointment.service.LoginSessionHandlerService;
import com.appointment.service.dto.request.AuthenticationRequestDto;
import com.appointment.service.dto.request.RefreshTokenRequestDto;
import com.appointment.service.dto.response.LoginResponseDto;
import com.appointment.service.dto.response.RefreshTokenResponse;
import com.appointment.service.mapper.LoginSessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.appointment.constant.ErrorCodeConstant.ERR_AUTHENTICATION_FAILED;
import static com.appointment.constant.ErrorCodeConstant.REFRESH_TOKEN_EXPIRED;
import static com.appointment.constant.ErrorCodeConstant.REFRESH_TOKEN_INVALID;
import static com.appointment.constant.ErrorCodeConstant.UNAUTHORIZED;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LoginSessionHandlerService loginSessionHandlerService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginSessionMapper loginSessionMapper;

    @Override
    @Transactional
    public LoginResponseDto authenticate(AuthenticationRequestDto request) {
        log.info("Start authentication request {}", request);
        Authentication authenticate;

        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    request.getEmployeeId(), request.getPassword());
            authenticate = authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            log.error("Authentication failure exception", e);
            throw new BadRequestException(e.getMessage());
        }

        if (!(authenticate.getPrincipal() instanceof CustomUserDetail)) {
            log.warn("Authentication failed - user not found");
            throw new BadCredentialsException(ERR_AUTHENTICATION_FAILED);
        }

        JwtTokenDto jwtToken = handleSuccessfulAuthentication(authenticate, request);
        return LoginResponseDto.builder()
                .isAuthenticated(true)
                .jwtTokenDto(jwtToken)
                .build();
    }

    private JwtTokenDto handleSuccessfulAuthentication(Authentication authentication, AuthenticationRequestDto dto) {
        JwtTokenDto jwtToken = Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .filter(CustomUserDetail.class::isInstance)
                .map(CustomUserDetail.class::cast)
                .map(jwtTokenProvider::generateLoginToken)
                .orElseThrow(() -> {
                    log.warn("Authentication object is not instance of CustomUserDetail");
                    return new UnauthorizedException(UNAUTHORIZED);
                });
        Optional.ofNullable(loginSessionMapper.toLoginSessionRequest(jwtToken))
                .map(sessionDto -> loginSessionMapper.toLoginSessionRequest(sessionDto, dto))
                .ifPresent(loginSessionHandlerService::saveLoginSession);

        log.info("End authentication request");
        return jwtToken;
    }

    @Override
    public RefreshTokenResponse refreshToken(RefreshTokenRequestDto request) {
        log.debug("Refreshing token for refresh token: {}", request.getRefreshToken());

        var tokenType = jwtTokenProvider.validateToken(request.getRefreshToken());
        if (tokenType == TokenValidationType.EXPIRED) {
            throw new BadRequestException(REFRESH_TOKEN_EXPIRED);
        }

        if (tokenType == TokenValidationType.INVALID_FORMAT || tokenType == TokenValidationType.INVALID_SIGNATURE) {
            throw new BadRequestException(REFRESH_TOKEN_INVALID);
        }

        var tokenPayload = jwtTokenProvider.parseJwtToken(request.getRefreshToken());

        if (!Boolean.TRUE.equals(tokenPayload.getIsRefreshToken())) {
            throw new BadRequestException(ErrorCodeConstant.INVALID_TOKEN_TYPE);
        }

        JwtTokenDto newJwt = jwtTokenProvider.generateFromToken(tokenPayload);

        Optional.ofNullable(newJwt)
                .map(loginSessionMapper::toLoginSessionRequest)
                .ifPresent(loginSessionHandlerService::renewExpiredSession);

        RefreshTokenResponse response = RefreshTokenResponse.builder()
                .accessToken(newJwt.getToken())
                .refreshToken(newJwt.getRefreshToken())
                .tokenType("Bearer")
                .expiresIn(newJwt.getExpiredTime().toEpochMilli())
                .build();

        log.info("Token refresh successful for user: {}", newJwt.getUsername());
        return response;
    }
}
