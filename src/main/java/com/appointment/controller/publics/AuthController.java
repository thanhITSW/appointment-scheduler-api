package com.appointment.controller.publics;

import com.appointment.constant.CommonConstant;
import com.appointment.service.AuthService;
import com.appointment.service.CookieService;
import com.appointment.service.dto.request.AuthenticationRequestDto;
import com.appointment.service.dto.request.RefreshTokenRequestDto;
import com.appointment.service.dto.response.LoginResponseDto;
import com.appointment.service.dto.response.RefreshTokenResponse;
import com.appointment.utils.RequestUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody AuthenticationRequestDto request,
                                                  @RequestHeader HttpHeaders requestHeaders) {
        extractingClientInformation(request, requestHeaders);
        LoginResponseDto jwtData = authService.authenticate(request);

        HttpHeaders headers = new HttpHeaders();
        if (jwtData.isAuthenticated()) {
            headers.add(HttpHeaders.SET_COOKIE,
                    cookieService.generateSessionCookie(CommonConstant.TOKEN_COOKIE_NAME,
                            jwtData.getJwtTokenDto().getSessionId()));
        }

        return new ResponseEntity<>(jwtData, headers, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequestDto request) {
        log.info("Token refresh attempt");
        RefreshTokenResponse response = authService.refreshToken(request);
        log.info("Token refresh successful");
        return ResponseEntity.ok(response);
    }

    private void extractingClientInformation(AuthenticationRequestDto requestDto, HttpHeaders headers) {
        final String ipAddress = RequestUtils.getIpAddressFromHeader(headers);
        final String userAgent = headers.getFirst(HttpHeaders.USER_AGENT);
        final String location = "Unknown";
        log.info("Client IP: {}, User-Agent: {}, Location: {}", ipAddress, userAgent, location);

        requestDto.setIpAddress(ipAddress);
        requestDto.setUserAgent(userAgent);
        requestDto.setLocation(location);
    }
}
