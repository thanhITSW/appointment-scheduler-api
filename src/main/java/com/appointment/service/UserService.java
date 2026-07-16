package com.appointment.service;

import java.time.Instant;

import com.appointment.entity.User;
import com.appointment.enumeration.UserRole;
import com.appointment.service.dto.request.ChangePasswordRequestDto;
import com.appointment.service.dto.request.CreateUserRequestDto;
import com.appointment.service.dto.request.DeleteUserRequestDto;
import com.appointment.service.dto.request.EditUserRequestDto;
import com.appointment.service.dto.request.UpdateUserRequestDto;
import com.appointment.service.dto.response.UserDetailResponseDto;

public interface UserService {

    void createNewUser(CreateUserRequestDto createNewUserDto);
    void editUser(EditUserRequestDto createNewUserDto);
    void deleteUser(Long userId);
    UserDetailResponseDto getCurrentUserLoginInfo(Long userId);
    
    // Updated user management methods
    void deleteUserWithAdminConfirmation(Long userId, DeleteUserRequestDto request);

    void updateUserByUserId(Long id, UpdateUserRequestDto updateUserRequestDto);
    
    // Password management
    void changePassword(Long userId, ChangePasswordRequestDto request);
    void savePasswordHistory(User user, String passwordHash);
    Instant getExpiryDaysForRole(UserRole role);
}
