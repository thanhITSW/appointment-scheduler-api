package com.appointment.service;

import com.appointment.service.dto.request.AuthenticationRequestDto;
import com.appointment.service.dto.request.RefreshTokenRequestDto;
import com.appointment.service.dto.response.LoginResponseDto;
import com.appointment.service.dto.response.RefreshTokenResponse;

public interface AuthService {

    LoginResponseDto authenticate(AuthenticationRequestDto request);

    RefreshTokenResponse refreshToken(RefreshTokenRequestDto request);

}
