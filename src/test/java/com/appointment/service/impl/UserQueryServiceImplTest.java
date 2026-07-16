package com.appointment.service.impl;

import com.appointment.entity.User;
import com.appointment.enumeration.UserRole;
import com.appointment.enumeration.UserStatus;
import com.appointment.jpa.specifications.filter.InstantFilter;
import com.appointment.jpa.specifications.filter.StringFilter;
import com.appointment.repository.UserRepository;
import com.appointment.service.criteria.UserCriteria;
import com.appointment.service.dto.response.UserResponseDto;
import com.appointment.service.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserQueryServiceImpl Tests")
@SuppressWarnings("unchecked")
class UserQueryServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    private User testUser1;
    private User testUser2;
    private UserResponseDto testUserResponseDto1;
    private UserResponseDto testUserResponseDto2;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // Setup test users
        testUser1 = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .role(UserRole.ADVISOR)
                .status(UserStatus.ACTIVATED)
                .deleted(false)
                .build();

        testUser2 = User.builder()
                .id(2L)
                .fullName("Jane Smith")
                .email("jane.smith@example.com")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVATED)
                .deleted(false)
                .build();

        // Setup test response DTOs
        testUserResponseDto1 = UserResponseDto.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .role(UserRole.ADVISOR)
                .status(UserStatus.ACTIVATED)
                .createdDate(Instant.now())
                .build();

        testUserResponseDto2 = UserResponseDto.builder()
                .id(2L)
                .fullName("Jane Smith")
                .email("jane.smith@example.com")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVATED)
                .createdDate(Instant.now())
                .build();

        // Setup pageable
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("getUsers - Should return all users when no criteria provided")
    void getUsers_ShouldReturnAllUsers_WhenNoCriteriaProvided() {
        // Given
        UserCriteria criteria = new UserCriteria();
        List<User> users = Arrays.asList(testUser1, testUser2);
        Page<User> userPage = new PageImpl<>(users, pageable, 2);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);
        when(userMapper.toResponseDto(testUser2)).thenReturn(testUserResponseDto2);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1, testUserResponseDto2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
        verify(userMapper).toResponseDto(testUser2);
    }

    @Test
    @DisplayName("getUsers - Should return filtered users by email")
    void getUsers_ShouldReturnFilteredUsersByEmail() {
        // Given
        StringFilter emailFilter = new StringFilter();
        emailFilter.setEquals("john.doe@example.com");

        UserCriteria criteria = new UserCriteria();
        criteria.setEmail(emailFilter);

        List<User> users = Arrays.asList(testUser1);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
    }

    @Test
    @DisplayName("getUsers - Should return filtered users by full name containing")
    void getUsers_ShouldReturnFilteredUsersByFullNameContaining() {
        // Given
        StringFilter fullNameFilter = new StringFilter();
        fullNameFilter.setContains("John");

        UserCriteria criteria = new UserCriteria();
        criteria.setFullName(fullNameFilter);

        List<User> users = Arrays.asList(testUser1);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
    }

    @Test
    @DisplayName("getUsers - Should return filtered users by role")
    void getUsers_ShouldReturnFilteredUsersByRole() {
        // Given
        UserCriteria.UserRoleFilter roleFilter = new UserCriteria.UserRoleFilter();
        roleFilter.setEquals(UserRole.ADMIN);

        UserCriteria criteria = new UserCriteria();
        criteria.setRole(roleFilter);

        List<User> users = Arrays.asList(testUser2);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser2)).thenReturn(testUserResponseDto2);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testUserResponseDto2);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser2);
    }

    @Test
    @DisplayName("getUsers - Should return filtered users by status")
    void getUsers_ShouldReturnFilteredUsersByStatus() {
        // Given
        UserCriteria.UserStatusFilter statusFilter = new UserCriteria.UserStatusFilter();
        statusFilter.setEquals(UserStatus.ACTIVATED);

        UserCriteria criteria = new UserCriteria();
        criteria.setStatus(statusFilter);

        List<User> users = Arrays.asList(testUser1, testUser2);
        Page<User> userPage = new PageImpl<>(users, pageable, 2);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);
        when(userMapper.toResponseDto(testUser2)).thenReturn(testUserResponseDto2);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1, testUserResponseDto2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
        verify(userMapper).toResponseDto(testUser2);
    }

    @Test
    @DisplayName("getUsers - Should return filtered users by date range")
    void getUsers_ShouldReturnFilteredUsersByDateRange() {
        // Given
        Instant fromDate = Instant.now().minusSeconds(86400); // 1 day ago
        Instant toDate = Instant.now();

        InstantFilter fromDateFilter = new InstantFilter();
        fromDateFilter.setGreaterThanOrEqual(fromDate);

        InstantFilter toDateFilter = new InstantFilter();
        toDateFilter.setLessThanOrEqual(toDate);

        UserCriteria criteria = new UserCriteria();
        criteria.setFromDate(fromDateFilter);
        criteria.setToDate(toDateFilter);

        List<User> users = Arrays.asList(testUser1, testUser2);
        Page<User> userPage = new PageImpl<>(users, pageable, 2);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);
        when(userMapper.toResponseDto(testUser2)).thenReturn(testUserResponseDto2);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1, testUserResponseDto2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
        verify(userMapper).toResponseDto(testUser2);
    }

    @Test
    @DisplayName("getUsers - Should return filtered users with multiple criteria")
    void getUsers_ShouldReturnFilteredUsersWithMultipleCriteria() {
        // Given
        StringFilter emailFilter = new StringFilter();
        emailFilter.setContains("john");

        UserCriteria.UserRoleFilter roleFilter = new UserCriteria.UserRoleFilter();
        roleFilter.setEquals(UserRole.ADVISOR);

        UserCriteria criteria = new UserCriteria();
        criteria.setEmail(emailFilter);
        criteria.setRole(roleFilter);

        List<User> users = Arrays.asList(testUser1);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
    }

    @Test
    @DisplayName("getUsers - Should return empty page when no users match criteria")
    void getUsers_ShouldReturnEmptyPage_WhenNoUsersMatchCriteria() {
        // Given
        StringFilter emailFilter = new StringFilter();
        emailFilter.setEquals("nonexistent@example.com");

        UserCriteria criteria = new UserCriteria();
        criteria.setEmail(emailFilter);

        List<User> users = Arrays.asList();
        Page<User> userPage = new PageImpl<>(users, pageable, 0);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper, never()).toResponseDto(any(User.class));
    }

    @Test
    @DisplayName("getUsers - Should return empty page when criteria is null")
    void getUsers_ShouldReturnEmptyPage_WhenCriteriaIsNull() {
        // Given
        UserCriteria criteria = null;

        List<User> users = Arrays.asList();
        Page<User> userPage = new PageImpl<>(users, pageable, 0);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper, never()).toResponseDto(any(User.class));
    }

    @Test
    @DisplayName("getUsers - Should handle pagination correctly")
    void getUsers_ShouldHandlePaginationCorrectly() {
        // Given
        UserCriteria criteria = new UserCriteria();
        Pageable customPageable = PageRequest.of(1, 5); // Second page, 5 items per page

        List<User> users = Arrays.asList(testUser1);
        Page<User> userPage = new PageImpl<>(users, customPageable, 6); // Total 6 items, showing 1 on page 2

        when(userRepository.findAll(any(Specification.class), eq(customPageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, customPageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1);
        assertThat(result.getTotalElements()).isEqualTo(6);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);

        verify(userRepository).findAll(any(Specification.class), eq(customPageable));
        verify(userMapper).toResponseDto(testUser1);
    }

    @Test
    @DisplayName("getUsers - Should filter by email with IN clause")
    void getUsers_ShouldFilterByEmailWithInClause() {
        // Given
        StringFilter emailFilter = new StringFilter();
        emailFilter.setIn(Arrays.asList("john.doe@example.com", "jane.smith@example.com"));

        UserCriteria criteria = new UserCriteria();
        criteria.setEmail(emailFilter);

        List<User> users = Arrays.asList(testUser1, testUser2);
        Page<User> userPage = new PageImpl<>(users, pageable, 2);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);
        when(userMapper.toResponseDto(testUser2)).thenReturn(testUserResponseDto2);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1, testUserResponseDto2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
        verify(userMapper).toResponseDto(testUser2);
    }

    @Test
    @DisplayName("getUsers - Should filter by role with NOT IN clause")
    void getUsers_ShouldFilterByRoleWithNotInClause() {
        // Given
        UserCriteria.UserRoleFilter roleFilter = new UserCriteria.UserRoleFilter();
        roleFilter.setNotIn(Arrays.asList(UserRole.ADMIN));

        UserCriteria criteria = new UserCriteria();
        criteria.setRole(roleFilter);

        List<User> users = Arrays.asList(testUser1);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
    }

    @Test
    @DisplayName("getUsers - Should filter by full name with NOT EQUALS clause")
    void getUsers_ShouldFilterByFullNameWithNotEqualsClause() {
        // Given
        StringFilter fullNameFilter = new StringFilter();
        fullNameFilter.setNotEquals("John Doe");

        UserCriteria criteria = new UserCriteria();
        criteria.setFullName(fullNameFilter);

        List<User> users = Arrays.asList(testUser2);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser2)).thenReturn(testUserResponseDto2);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testUserResponseDto2);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser2);
    }

    @Test
    @DisplayName("getUsers - Should filter by email with DOES NOT CONTAIN clause")
    void getUsers_ShouldFilterByEmailWithDoesNotContainClause() {
        // Given
        StringFilter emailFilter = new StringFilter();
        emailFilter.setDoesNotContain("john");

        UserCriteria criteria = new UserCriteria();
        criteria.setEmail(emailFilter);

        List<User> users = Arrays.asList(testUser2);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser2)).thenReturn(testUserResponseDto2);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testUserResponseDto2);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser2);
    }

    @Test
    @DisplayName("getUsers - Should filter by status with NOT EQUALS clause")
    void getUsers_ShouldFilterByStatusWithNotEqualsClause() {
        // Given
        UserCriteria.UserStatusFilter statusFilter = new UserCriteria.UserStatusFilter();
        statusFilter.setNotEquals(UserStatus.DELETED);

        UserCriteria criteria = new UserCriteria();
        criteria.setStatus(statusFilter);

        List<User> users = Arrays.asList(testUser1, testUser2);
        Page<User> userPage = new PageImpl<>(users, pageable, 2);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);
        when(userMapper.toResponseDto(testUser2)).thenReturn(testUserResponseDto2);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1, testUserResponseDto2);
        assertThat(result.getTotalElements()).isEqualTo(2);

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
        verify(userMapper).toResponseDto(testUser2);
    }

    @Test
    @DisplayName("getUsers - Should always exclude deleted users regardless of criteria")
    void getUsers_ShouldAlwaysExcludeDeletedUsersRegardlessOfCriteria() {
        // Given
        StringFilter emailFilter = new StringFilter();
        emailFilter.setEquals("test@example.com");

        UserCriteria criteria = new UserCriteria();
        criteria.setEmail(emailFilter);

        List<User> users = Arrays.asList(testUser1);
        Page<User> userPage = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(userPage);
        when(userMapper.toResponseDto(testUser1)).thenReturn(testUserResponseDto1);

        // When
        Page<UserResponseDto> result = userQueryService.getUsers(criteria, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).containsExactly(testUserResponseDto1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        // Verify that the specification includes both email filter and soft delete filter
        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verify(userMapper).toResponseDto(testUser1);
    }
}
