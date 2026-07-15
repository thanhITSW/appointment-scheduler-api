package com.appointment.service.mapper;

import com.appointment.entity.LoginSession;
import com.appointment.security.dto.JwtTokenDto;
import com.appointment.service.dto.request.AuthenticationRequestDto;
import com.appointment.service.dto.request.LoginSessionRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LoginSessionMapper {
    @Mapping(target = "active", constant = "true")
    LoginSession toEntity(LoginSessionRequestDto dto);

    @Mapping(target = "expiresAt", source = "expiredTime")
    @Mapping(target = "tokenDetail.token", source = "token")
    @Mapping(target = "tokenDetail.refreshToken", source = "refreshToken")
    LoginSessionRequestDto toLoginSessionRequest(JwtTokenDto dto);

    LoginSessionRequestDto toLoginSessionRequest(@MappingTarget LoginSessionRequestDto dto,
            AuthenticationRequestDto authRequest);
}
