package com.appointment.service.impl;

import com.appointment.config.ApplicationProperties;
import com.appointment.entity.User;
import com.appointment.enumeration.UserRole;
import com.appointment.enumeration.UserStatus;
import com.appointment.exception.BadRequestException;
import com.appointment.repository.PasswordHistoryRepository;
import com.appointment.repository.UserRepository;
import com.appointment.security.handler.SecurityContextHandler;
import com.appointment.service.dto.request.CreateUserRequestDto;
import com.appointment.service.dto.request.DeleteUserRequestDto;
import com.appointment.service.dto.request.EditUserRequestDto;
import com.appointment.service.dto.response.UserDetailResponseDto;
import com.appointment.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.appointment.constant.ErrorCodeConstant.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private ApplicationProperties.SecurityProps securityProps;

    @Mock
    private ApplicationProperties.PasswordConfig passwordConfig;

    @Mock
    private PasswordHistoryRepository passwordHistoryRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private User adminUser;
    private UserDetailResponseDto testUserDetailResponseDto;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = User.builder()
                .id(1L)
                .fullName("Test User")
                .email("test@example.com")
                .password("encodedPassword")
                .role(UserRole.ADVISOR)
                .status(UserStatus.ACTIVATED)
                .note("Test note")
                .deleted(false)
                .blocked(false)
                .build();

        // Setup admin user
        adminUser = User.builder()
                .id(2L)
                .fullName("Admin User")
                .email("admin@example.com")
                .password("encodedAdminPassword")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVATED)
                .note("Admin note")
                .deleted(false)
                .blocked(false)
                .build();

        // Setup test response DTO
        testUserDetailResponseDto = new UserDetailResponseDto();
        testUserDetailResponseDto.setId(1L);
        testUserDetailResponseDto.setEmail("test@example.com");
        testUserDetailResponseDto.setFullName("Test User");
        testUserDetailResponseDto.setRole(UserRole.ADVISOR);
        testUserDetailResponseDto.setStatus(UserStatus.ACTIVATED);

        // Setup ApplicationProperties mocks - only when needed
        // when(applicationProperties.getSecurity()).thenReturn(securityProps);
        // when(securityProps.getPassword()).thenReturn(passwordConfig);
        // when(passwordConfig.getUserExpiryDays()).thenReturn(90);
        // when(passwordConfig.getAdminExpiryDays()).thenReturn(180);
    }

    @Test
    @DisplayName("createNewUser - Should successfully create new user")
    void createNewUser_ShouldSuccessfullyCreateNewUser() {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId("EMP001")
                .email("user@example.com")
                .fullName("Test User")
                .password("password123")
                .role(UserRole.ADVISOR)
                .build();

        when(userRepository.existsByEmployeeIdIgnoreCaseAndDeletedFalse("EMP001")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(applicationProperties.getSecurity()).thenReturn(securityProps);
        when(securityProps.getPassword()).thenReturn(passwordConfig);

        // When
        userService.createNewUser(request);

        // Then
        verify(userRepository).existsByEmployeeIdIgnoreCaseAndDeletedFalse("EMP001");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }



    @Test
    @DisplayName("createNewUser - Should throw BadRequestException when ApplicationProperties is not configured")
    void createNewUser_ShouldThrowBadRequestException_WhenApplicationPropertiesNotConfigured() {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId("EMP001")
                .email("user@example.com")
                .fullName("Test User")
                .password("password123")
                .role(UserRole.ADVISOR)
                .build();

        when(userRepository.existsByEmployeeIdIgnoreCaseAndDeletedFalse("EMP001")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        // Don't setup applicationProperties.getSecurity() to simulate null

        // When & Then
        assertThatThrownBy(() -> userService.createNewUser(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Failed to create user: Cannot invoke");

        verify(userRepository).existsByEmployeeIdIgnoreCaseAndDeletedFalse("EMP001");
        verify(passwordEncoder).encode("password123");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("createNewUser - Should throw BadRequestException when employee ID already exists")
    void createNewUser_ShouldThrowBadRequestException_WhenEmployeeIdAlreadyExists() {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId("EMP001")
                .email("user@example.com")
                .fullName("Test User")
                .password("password123")
                .role(UserRole.ADVISOR)
                .build();

        when(userRepository.existsByEmployeeIdIgnoreCaseAndDeletedFalse("EMP001")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createNewUser(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ERR_EMPLOYEE_ID_DUPLICATED);

        verify(userRepository).existsByEmployeeIdIgnoreCaseAndDeletedFalse("EMP001");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("editUser - Should successfully edit user with all fields")
    void editUser_ShouldSuccessfullyEditUserWithAllFields() {
        // Given
        EditUserRequestDto request = EditUserRequestDto.builder()
                .id(1L)
                .role(UserRole.ADMIN)
                .password("newPassword123")
                .note("Updated note")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.editUser(request);

        // Then
        assertThat(testUser.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(testUser.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(testUser.getNote()).isEqualTo("Updated note");

        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("editUser - Should successfully edit user with only note")
    void editUser_ShouldSuccessfullyEditUserWithOnlyNote() {
        // Given
        EditUserRequestDto request = EditUserRequestDto.builder()
                .id(1L)
                .role(null)
                .password(null)
                .note("Updated note only")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.editUser(request);

        // Then
        assertThat(testUser.getNote()).isEqualTo("Updated note only");
        assertThat(testUser.getRole()).isEqualTo(UserRole.ADVISOR); // Should remain unchanged
        assertThat(testUser.getPassword()).isEqualTo("encodedPassword"); // Should remain unchanged

        verify(userRepository).findById(1L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("editUser - Should successfully edit user with only role")
    void editUser_ShouldSuccessfullyEditUserWithOnlyRole() {
        // Given
        EditUserRequestDto request = EditUserRequestDto.builder()
                .id(1L)
                .role(UserRole.ADMIN)
                .password(null)
                .note(null)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.editUser(request);

        // Then
        assertThat(testUser.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(testUser.getPassword()).isEqualTo("encodedPassword"); // Should remain unchanged
        assertThat(testUser.getNote()).isNull(); // Should be set to null from request

        verify(userRepository).findById(1L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("editUser - Should successfully edit user with only password")
    void editUser_ShouldSuccessfullyEditUserWithOnlyPassword() {
        // Given
        EditUserRequestDto request = EditUserRequestDto.builder()
                .id(1L)
                .role(null)
                .password("newPassword123")
                .note(null)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.editUser(request);

        // Then
        assertThat(testUser.getPassword()).isEqualTo("encodedNewPassword");
        assertThat(testUser.getRole()).isEqualTo(UserRole.ADVISOR); // Should remain unchanged
        assertThat(testUser.getNote()).isNull(); // Should be set to null from request

        verify(userRepository).findById(1L);
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("editUser - Should throw BadRequestException when user not found")
    void editUser_ShouldThrowBadRequestException_WhenUserNotFound() {
        // Given
        EditUserRequestDto request = EditUserRequestDto.builder()
                .id(999L)
                .role(UserRole.ADMIN)
                .password("newPassword123")
                .note("Updated note")
                .build();

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.editUser(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ERR_USER_INVALID);

        verify(userRepository).findById(999L);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("deleteUser - Should successfully delete user")
    void deleteUser_ShouldSuccessfullyDeleteUser() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUser(1L);

        // Then
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(testUser.isDeleted()).isTrue();

        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("deleteUser - Should throw BadRequestException when user not found")
    void deleteUser_ShouldThrowBadRequestException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ERR_USER_INVALID);

        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getCurrentUserLoginInfo - Should successfully return user login info")
    void getCurrentUserLoginInfo_ShouldSuccessfullyReturnUserLoginInfo() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toUserDetailDto(testUser)).thenReturn(testUserDetailResponseDto);

        // When
        UserDetailResponseDto result = userService.getCurrentUserLoginInfo(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFullName()).isEqualTo("Test User");
        assertThat(result.getRole()).isEqualTo(UserRole.ADVISOR);
        assertThat(result.getStatus()).isEqualTo(UserStatus.ACTIVATED);

        verify(userRepository).findById(1L);
        verify(userMapper).toUserDetailDto(testUser);
    }

    @Test
    @DisplayName("getCurrentUserLoginInfo - Should throw BadRequestException when user not found")
    void getCurrentUserLoginInfo_ShouldThrowBadRequestException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getCurrentUserLoginInfo(999L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ERR_USER_NOT_FOUND);

        verify(userRepository).findById(999L);
        verify(userMapper, never()).toUserDetailDto(any(User.class));
    }

    @Test
    @DisplayName("deleteUserWithAdminConfirmation - Should successfully delete user with admin confirmation")
    void deleteUserWithAdminConfirmation_ShouldSuccessfullyDeleteUserWithAdminConfirmation() {
        // Given
        DeleteUserRequestDto request = DeleteUserRequestDto.builder()
                .adminPassword("adminPassword123")
                .deleteReason("Admin deletion")
                .build();

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUserWithAdminConfirmation(1L, request);

        // Then
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(testUser.isDeleted()).isTrue();

        verify(userRepository).findByIdAndDeletedFalse(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("deleteUserWithAdminConfirmation - Should throw BadRequestException when user to delete not found")
    void deleteUserWithAdminConfirmation_ShouldThrowBadRequestException_WhenUserToDeleteNotFound() {
        // Given
        DeleteUserRequestDto request = DeleteUserRequestDto.builder()
                .adminPassword("adminPassword123")
                .deleteReason("Admin deletion")
                .build();

        when(userRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.deleteUserWithAdminConfirmation(999L, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ERR_USER_NOT_FOUND);

        verify(userRepository).findByIdAndDeletedFalse(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("deleteUserWithAdminConfirmation - Should successfully delete user without delete reason")
    void deleteUserWithAdminConfirmation_ShouldSuccessfullyDeleteUserWithoutDeleteReason() {
        // Given
        DeleteUserRequestDto request = DeleteUserRequestDto.builder()
                .adminPassword("adminPassword123")
                .deleteReason(null)
                .build();

        when(userRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUserWithAdminConfirmation(1L, request);

        // Then
        assertThat(testUser.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(testUser.isDeleted()).isTrue();

        verify(userRepository).findByIdAndDeletedFalse(1L);
        verify(userRepository).save(testUser);
    }

}
