package com.appointment.service.dto.response;

import com.appointment.security.dto.JwtTokenDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {

    private boolean isAuthenticated;
    private JwtTokenDto jwtTokenDto;

}
