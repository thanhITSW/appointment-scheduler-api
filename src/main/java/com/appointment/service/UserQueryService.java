package com.appointment.service;

import com.appointment.service.criteria.UserCriteria;
import com.appointment.service.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryService {
    Page<UserResponseDto> getUsers(UserCriteria criteria, Pageable pageable);
}

