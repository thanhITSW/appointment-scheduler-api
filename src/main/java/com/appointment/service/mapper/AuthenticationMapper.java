package com.appointment.service.mapper;

import com.appointment.entity.User;
import com.appointment.security.model.CustomUserDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthenticationMapper {
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "username", source = "employeeId")
    @Mapping(target = "employeeId", source = "employeeId")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "isUserBlocked", expression = "java(user.isUserBlocked())")
    @Mapping(target = "isAccountLocked", expression = "java(user.isAccountLocked())")
    @Mapping(target = "isPasswordExpired", expression = "java(user.isPasswordExpired())")
    @Mapping(target = "enabled", expression = "java(user.isUserEnable())")
    CustomUserDetail toUserDetail(User user);
}
