package com.appointment.service.mapper;

import com.appointment.entity.User;
import com.appointment.service.dto.response.UserDetailResponseDto;
import com.appointment.service.dto.response.UserResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "fullName", source = "fullName")
    UserResponseDto toResponseDto(User user);

    @Mapping(target = "fullName", source = "fullName")
    UserDetailResponseDto toUserDetailDto(User user);
}
