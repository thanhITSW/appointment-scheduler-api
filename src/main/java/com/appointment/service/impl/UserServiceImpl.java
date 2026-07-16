package com.appointment.service.impl;

import com.appointment.config.ApplicationProperties;
import com.appointment.entity.PasswordHistory;
import com.appointment.entity.User;
import com.appointment.enumeration.UserRole;
import com.appointment.enumeration.UserStatus;
import com.appointment.exception.BadRequestException;
import com.appointment.repository.PasswordHistoryRepository;
import com.appointment.repository.UserRepository;
import com.appointment.service.UserService;
import com.appointment.service.dto.request.ChangePasswordRequestDto;
import com.appointment.service.dto.request.CreateUserRequestDto;
import com.appointment.service.dto.request.DeleteUserRequestDto;
import com.appointment.service.dto.request.EditUserRequestDto;
import com.appointment.service.dto.request.UpdateUserRequestDto;
import com.appointment.service.dto.response.UserDetailResponseDto;
import com.appointment.service.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.appointment.constant.ErrorCodeConstant.ERR_EMPLOYEE_ID_DUPLICATED;
import static com.appointment.constant.ErrorCodeConstant.ERR_OLD_PASSWORD_INCORRECT;
import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_NOT_MATCH;
import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_REUSED;
import static com.appointment.constant.ErrorCodeConstant.ERR_USER_INVALID;
import static com.appointment.constant.ErrorCodeConstant.ERR_USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final ApplicationProperties applicationProperties;
    private final PasswordHistoryRepository passwordHistoryRepository;

    @Override
    @Transactional
    public void createNewUser(CreateUserRequestDto request) {
        log.info("Creating new user with employeeId: {}", request.getEmployeeId());

        // Check if employee id already exists
        if (userRepository.existsByEmployeeIdIgnoreCaseAndDeletedFalse(request.getEmployeeId())) {
            throw new BadRequestException(ERR_EMPLOYEE_ID_DUPLICATED);
        }

        try {
            String hashedPassword = passwordEncoder.encode(request.getPassword());

            // Create user - sensitive data will be automatically encrypted by the converter
            User user = User.builder()
                    .status(UserStatus.ACTIVATED)
                    .password(hashedPassword)
                    .role(request.getRole())
                    .email(request.getEmail())
                    .fullName(request.getFullName())
                    .passwordChangedAt(Instant.now())
                    .employeeId(request.getEmployeeId())
                    .twoFaEnabled(false)
                    .passwordExpiresAt(getExpiryDaysForRole(request.getRole()))
                    .build();

            // Save user
            User savedUser = userRepository.save(user);
            // Save password history
            savePasswordHistory(savedUser, hashedPassword);

            log.info("User created successfully with ID: {} and employeeId: {}", savedUser.getId(), savedUser.getEmployeeId());

        } catch (Exception e) {
            log.error("Failed to create user: {}", request.getEmployeeId(), e);
            throw new BadRequestException("Failed to create user: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void editUser(EditUserRequestDto request) {
        log.info("Editing user with ID: {}", request.getId());

        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new BadRequestException(ERR_USER_INVALID));

        user.setNote(request.getNote());
        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (Objects.nonNull(request.getRole())) {
            user.setRole(request.getRole());
        }

        userRepository.save(user);
        log.info("User updated successfully with ID: {}", request.getId());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ERR_USER_INVALID));

        user.setStatus(UserStatus.DELETED);
        user.setDeleted(true);
        User saved = userRepository.save(user);
        /// TODO: Handle session is invalid when delete user

        log.info("Created delete history for user: {}", saved.getEmail());
    }

    @Override
    public UserDetailResponseDto getCurrentUserLoginInfo(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ERR_USER_NOT_FOUND));
        return userMapper.toUserDetailDto(user);
    }


    @Override
    @Transactional
    public void deleteUserWithAdminConfirmation(Long userId, DeleteUserRequestDto request) {
        // Get current admin user ID from JWT token
//        Long adminUserId = SecurityContextHandler.extractUserId()
//                .orElseThrow(() -> new BadRequestException("Unable to extract admin user ID from token"));

//        User adminUser = userRepository.findByIdAndDeletedFalse(adminUserId)
//                .orElseThrow(() -> new BadRequestException(ERR_USER_NOT_FOUND));

//        // Validate admin password
//        if (!passwordEncoder.matches(request.getAdminPassword(), adminUser.getPassword())) {
//            throw new BadRequestException(ERR_ADMIN_PASSWORD_INCORRECT);
//        }

        // Get user to delete
        User userToDelete = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BadRequestException(ERR_USER_NOT_FOUND));

        // Soft delete the user
        userToDelete.setStatus(UserStatus.DELETED);
        userToDelete.setDeleted(true);
        userRepository.save(userToDelete);
        /// TODO: Handle session is invalid when delete user

        log.info("User {} deleted",
                userToDelete.getEmail());
    }

    @Override
    @Transactional
    public void updateUserByUserId(Long id, UpdateUserRequestDto updateUserRequestDto) {
        log.info("Starting update for user ID: {}, new employeeID: {}", id, updateUserRequestDto.getEmployeeId());

        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BadRequestException(ERR_USER_NOT_FOUND));

        // Check if employee id already exists
        if(!user.getEmployeeId().equals(updateUserRequestDto.getEmployeeId())){
            if (userRepository.existsByEmployeeIdIgnoreCaseAndDeletedFalse(updateUserRequestDto.getEmployeeId())) {
                throw new BadRequestException(ERR_EMPLOYEE_ID_DUPLICATED);
            }
        }

        if (!passwordEncoder.matches(updateUserRequestDto.getPassword(), user.getPassword())) {

            // Check if new password matches any of the last 5 passwords
            if (isPasswordInHistory(user.getId(), updateUserRequestDto.getPassword())) {
                log.warn("User {} attempted to reuse a previous password", user.getEmployeeId());
                throw new BadRequestException(ERR_PASSWORD_REUSED);
            }

            String hashedPassword = passwordEncoder.encode(updateUserRequestDto.getPassword());
            user.setPassword(hashedPassword);
            user.setPasswordChangedAt(Instant.now());
            savePasswordHistory(user, hashedPassword);

        }

        user.setEmployeeId(updateUserRequestDto.getEmployeeId());
        userRepository.save(user);

    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequestDto request) {
        log.info("Changing password for user ID: {}", userId);

        // Validate password confirmation
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BadRequestException(ERR_PASSWORD_NOT_MATCH);
        }

        // Get user
        User user = userRepository.findByIdAndDeletedFalse(userId)
                .orElseThrow(() -> new BadRequestException(ERR_USER_NOT_FOUND));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Current password verification failed for user: {}", user.getEmail());
            throw new BadRequestException(ERR_OLD_PASSWORD_INCORRECT);
        }

        // Check if new password matches any of the last 5 passwords
        if (isPasswordInHistory(user.getId(), request.getNewPassword())) {
            log.warn("User {} attempted to reuse a previous password", user.getEmail());
            throw new BadRequestException(ERR_PASSWORD_REUSED);
        }

        String hashedPassword = passwordEncoder.encode(request.getNewPassword());

        // Save password history
        savePasswordHistory(user, hashedPassword);

        user.setPassword(hashedPassword);
        user.setPasswordChangedAt(Instant.now());
        user.setPasswordExpiresAt(getExpiryDaysForRole(user.getRole()));
        
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", user.getEmail());
    }

    private boolean isPasswordInHistory(Long userId, String newPassword) {
        // Get the last 5 password hashes for this user
        List<PasswordHistory> passwordHistories = passwordHistoryRepository.findRecentPasswordHistory(userId,
                PageRequest.of(0, 5));

        // Check if the new password matches any of the previous passwords
        for (PasswordHistory history : passwordHistories) {
            if (passwordEncoder.matches(newPassword, history.getPasswordHash())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void savePasswordHistory(User user, String passwordHash) {
        PasswordHistory history = PasswordHistory.builder()
                .user(user)
                .passwordHash(passwordHash)
                .createdAt(Instant.now())
                .active(true)
                .build();
        passwordHistoryRepository.save(history);

        // Clean up old password history (keep only recent ones)
        passwordHistoryRepository.deleteOldPasswordHistory(user.getId(), 5);
    }

    @Override
    public Instant getExpiryDaysForRole(UserRole role) {
        long expiryDay = (role == UserRole.ADMIN || role == UserRole.MANAGER) ?
                applicationProperties.getSecurity().getPassword().getAdminExpiryDays() :
                applicationProperties.getSecurity().getPassword().getUserExpiryDays();

        return Instant.now().plusSeconds(expiryDay * 24 * 60 * 60);
    }
}
