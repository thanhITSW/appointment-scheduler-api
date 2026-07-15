package com.appointment.controller.privates;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.appointment.enumeration.UserRole;
import com.appointment.enumeration.UserStatus;
import com.appointment.service.UserQueryService;
import com.appointment.service.UserService;
import com.appointment.service.criteria.UserCriteria;
import com.appointment.service.dto.request.CreateUserRequestDto;
import com.appointment.service.dto.request.DeleteUserRequestDto;
import com.appointment.service.dto.response.UserResponseDto;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserManagementPrivateController Tests")
class UserManagementPrivateControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private UserManagementPrivateController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();
    }

    // User Creation Tests

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() throws Exception {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId("EMP001")
                .email("user@example.com")
                .fullName("Test User")
                .password("Password123!")
                .role(UserRole.ADVISOR)
                .build();

        doNothing().when(userService).createNewUser(any(CreateUserRequestDto.class));

        // When & Then
        mockMvc.perform(post("/api/v1/private/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createNewUser(any(CreateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating user with invalid employeeId")
    void shouldReturn400WhenCreatingUserWithInvalidEmployeeId() throws Exception {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId(null) // Invalid: null employeeId
                .password("Password123!")
                .role(UserRole.ADVISOR)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/private/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createNewUser(any(CreateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating user with blank employeeId")
    void shouldReturn400WhenCreatingUserWithBlankEmployeeId() throws Exception {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId("") // Invalid: blank employeeId
                .password("Password123!")
                .role(UserRole.ADVISOR)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/private/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createNewUser(any(CreateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating user with short password")
    void shouldReturn400WhenCreatingUserWithShortPassword() throws Exception {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId("EMP001")
                .email("user@example.com")
                .fullName("Test User")
                .password("12345") // Invalid: too short (min 8 chars)
                .role(UserRole.ADVISOR)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/private/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createNewUser(any(CreateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating user with invalid password format")
    void shouldReturn400WhenCreatingUserWithInvalidPasswordFormat() throws Exception {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId("EMP001")
                .email("user@example.com")
                .fullName("Test User")
                .password("password123") // Invalid: missing uppercase, numbers, special chars
                .role(UserRole.ADVISOR)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/private/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createNewUser(any(CreateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when creating user with null password")
    void shouldReturn400WhenCreatingUserWithNullPassword() throws Exception {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId("EMP001")
                .email("user@example.com")
                .fullName("Test User")
                .password(null) // Invalid: null password
                .role(UserRole.ADVISOR)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/private/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createNewUser(any(CreateUserRequestDto.class));
    }

    @Test
    @DisplayName("Should create admin user successfully")
    void shouldCreateAdminUserSuccessfully() throws Exception {
        // Given
        CreateUserRequestDto request = CreateUserRequestDto.builder()
                .employeeId("EMP002")
                .email("user@example.com")
                .fullName("Test User")
                .password("Admin123!")
                .role(UserRole.ADMIN)
                .build();

        doNothing().when(userService).createNewUser(any(CreateUserRequestDto.class));

        // When & Then
        mockMvc.perform(post("/api/v1/private/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createNewUser(any(CreateUserRequestDto.class));
    }

    // User Deletion Tests

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() throws Exception {
        // Given
        Long userId = 1L;
        DeleteUserRequestDto request = DeleteUserRequestDto.builder()
                .adminPassword("admin123")
                .deleteReason("User requested deletion")
                .build();

        doNothing().when(userService).deleteUserWithAdminConfirmation(eq(userId), any(DeleteUserRequestDto.class));

        // When & Then
        mockMvc.perform(patch("/api/v1/private/users/{id}/delete-confirm", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserWithAdminConfirmation(eq(userId), any(DeleteUserRequestDto.class));
    }

    @Test
    @DisplayName("Should return 400 when deleting user with blank admin password")
    void shouldReturn400WhenDeletingUserWithBlankAdminPassword() throws Exception {
        // Given
        Long userId = 1L;
        DeleteUserRequestDto request = DeleteUserRequestDto.builder()
                .adminPassword("") // Invalid: blank password
                .deleteReason("User requested deletion")
                .build();

        // When & Then
        mockMvc.perform(patch("/api/v1/private/users/{id}/delete-confirm", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).deleteUserWithAdminConfirmation(eq(userId), any(DeleteUserRequestDto.class));
    }

    @Test
    @DisplayName("Should delete user without reason successfully")
    void shouldDeleteUserWithoutReasonSuccessfully() throws Exception {
        // Given
        Long userId = 1L;
        DeleteUserRequestDto request = DeleteUserRequestDto.builder()
                .adminPassword("admin123")
                .deleteReason(null) // Optional field
                .build();

        doNothing().when(userService).deleteUserWithAdminConfirmation(eq(userId), any(DeleteUserRequestDto.class));

        // When & Then
        mockMvc.perform(patch("/api/v1/private/users/{id}/delete-confirm", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUserWithAdminConfirmation(eq(userId), any(DeleteUserRequestDto.class));
    }

    // User Query Tests

    @Test
    @DisplayName("Should get users with pagination successfully")
    void shouldGetUsersWithPaginationSuccessfully() throws Exception {
        // Given
        UserResponseDto user1 = UserResponseDto.builder()
                .id(1L)
                .email("user1@example.com")
                .fullName("User One")
                .role(UserRole.ADVISOR)
                .status(UserStatus.ACTIVATED)
                .createdDate(Instant.now())
                .createdBy("admin")
                .note("Regular user")
                .build();

        UserResponseDto user2 = UserResponseDto.builder()
                .id(2L)
                .email("user2@example.com")
                .fullName("User Two")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVATED)
                .createdDate(Instant.now())
                .createdBy("system")
                .note("Admin user")
                .build();

        List<UserResponseDto> users = Arrays.asList(user1, user2);
        Page<UserResponseDto> page = new PageImpl<>(users, PageRequest.of(0, 20), 2);

        when(userQueryService.getUsers(any(UserCriteria.class), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/private/users")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdDate,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[0].fullName").value("User One"))
                .andExpect(jsonPath("$[0].role").value("ADVISOR"))
                .andExpect(jsonPath("$[0].status").value("ACTIVATED"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));

        verify(userQueryService, times(1)).getUsers(any(UserCriteria.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get users with filtering successfully")
    void shouldGetUsersWithFilteringSuccessfully() throws Exception {
        // Given
        UserResponseDto user = UserResponseDto.builder()
                .id(1L)
                .email("admin@example.com")
                .fullName("Admin User")
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVATED)
                .createdDate(Instant.now())
                .createdBy("system")
                .build();

        List<UserResponseDto> users = Arrays.asList(user);
        Page<UserResponseDto> page = new PageImpl<>(users, PageRequest.of(0, 20), 1);

        when(userQueryService.getUsers(any(UserCriteria.class), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/private/users")
                        .param("email.contains", "admin")
                        .param("role.equals", "ADMIN")
                        .param("status.equals", "ACTIVATED")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].email").value("admin@example.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));

        verify(userQueryService, times(1)).getUsers(any(UserCriteria.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get users with date range filtering successfully")
    void shouldGetUsersWithDateRangeFilteringSuccessfully() throws Exception {
        // Given
        UserResponseDto user = UserResponseDto.builder()
                .id(1L)
                .email("user@example.com")
                .fullName("Test User")
                .role(UserRole.ADVISOR)
                .status(UserStatus.ACTIVATED)
                .createdDate(Instant.now())
                .createdBy("admin")
                .build();

        List<UserResponseDto> users = Arrays.asList(user);
        Page<UserResponseDto> page = new PageImpl<>(users, PageRequest.of(0, 20), 1);

        when(userQueryService.getUsers(any(UserCriteria.class), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/private/users")
                        .param("fromDate.greaterThan", "2024-01-01T00:00:00Z")
                        .param("toDate.lessThan", "2024-12-31T23:59:59Z")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(userQueryService, times(1)).getUsers(any(UserCriteria.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should handle empty user list")
    void shouldHandleEmptyUserList() throws Exception {
        // Given
        Page<UserResponseDto> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 20), 0);

        when(userQueryService.getUsers(any(UserCriteria.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(get("/api/v1/private/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(userQueryService, times(1)).getUsers(any(UserCriteria.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get users with different roles successfully")
    void shouldGetUsersWithDifferentRolesSuccessfully() throws Exception {
        // Given
        List<UserResponseDto> users = Arrays.asList(
                UserResponseDto.builder().id(1L).email("user@example.com").fullName("User").role(UserRole.ADVISOR).status(UserStatus.ACTIVATED).createdDate(Instant.now()).build(),
                UserResponseDto.builder().id(2L).email("staff@example.com").fullName("Staff").role(UserRole.ADVISOR).status(UserStatus.ACTIVATED).createdDate(Instant.now()).build(),
                UserResponseDto.builder().id(3L).email("manager@example.com").fullName("Manager").role(UserRole.MANAGER).status(UserStatus.ACTIVATED).createdDate(Instant.now()).build(),
                UserResponseDto.builder().id(4L).email("admin@example.com").fullName("Admin").role(UserRole.ADMIN).status(UserStatus.ACTIVATED).createdDate(Instant.now()).build()
        );

        Page<UserResponseDto> page = new PageImpl<>(users, PageRequest.of(0, 20), 4);

        when(userQueryService.getUsers(any(UserCriteria.class), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/private/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].role").value("ADVISOR"))
                .andExpect(jsonPath("$[1].role").value("ADVISOR"))
                .andExpect(jsonPath("$[2].role").value("MANAGER"))
                .andExpect(jsonPath("$[3].role").value("ADMIN"));

        verify(userQueryService, times(1)).getUsers(any(UserCriteria.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get users with different statuses successfully")
    void shouldGetUsersWithDifferentStatusesSuccessfully() throws Exception {
        // Given
        List<UserResponseDto> users = Arrays.asList(
                UserResponseDto.builder().id(1L).email("active@example.com").fullName("Active User").role(UserRole.ADVISOR).status(UserStatus.ACTIVATED).createdDate(Instant.now()).build(),
                UserResponseDto.builder().id(2L).email("inactive@example.com").fullName("Inactive User").role(UserRole.ADVISOR).status(UserStatus.INACTIVE).createdDate(Instant.now()).build(),
                UserResponseDto.builder().id(3L).email("blocked@example.com").fullName("Blocked User").role(UserRole.ADVISOR).status(UserStatus.BLOCKED).createdDate(Instant.now()).build()
        );

        Page<UserResponseDto> page = new PageImpl<>(users, PageRequest.of(0, 20), 3);

        when(userQueryService.getUsers(any(UserCriteria.class), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/private/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].status").value("ACTIVATED"))
                .andExpect(jsonPath("$[1].status").value("INACTIVE"))
                .andExpect(jsonPath("$[2].status").value("BLOCKED"));

        verify(userQueryService, times(1)).getUsers(any(UserCriteria.class), any(Pageable.class));
    }

    // Edge Cases and Error Handling Tests

    @Test
    @DisplayName("Should handle large user list with pagination")
    void shouldHandleLargeUserListWithPagination() throws Exception {
        // Given
        List<UserResponseDto> users = new java.util.ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            users.add(UserResponseDto.builder()
                    .id((long) i)
                    .email("user" + i + "@example.com")
                    .fullName("User " + i)
                    .role(UserRole.ADVISOR)
                    .status(UserStatus.ACTIVATED)
                    .createdDate(Instant.now())
                    .build());
        }

        Page<UserResponseDto> page = new PageImpl<>(users, PageRequest.of(0, 50), 50);

        when(userQueryService.getUsers(any(UserCriteria.class), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/private/users")
                        .param("page", "0")
                        .param("size", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(50));

        verify(userQueryService, times(1)).getUsers(any(UserCriteria.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Should handle invalid pagination parameters gracefully")
    void shouldHandleInvalidPaginationParametersGracefully() throws Exception {
        // Given
        UserResponseDto user = UserResponseDto.builder()
                .id(1L)
                .email("user@example.com")
                .fullName("Test User")
                .role(UserRole.ADVISOR)
                .status(UserStatus.ACTIVATED)
                .createdDate(Instant.now())
                .build();

        List<UserResponseDto> users = Arrays.asList(user);
        Page<UserResponseDto> page = new PageImpl<>(users, PageRequest.of(0, 20), 1);

        when(userQueryService.getUsers(any(UserCriteria.class), any(Pageable.class)))
                .thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/private/users")
                        .param("page", "-1") // Invalid page number
                        .param("size", "0")) // Invalid size
                .andExpect(status().isOk()) // Spring handles invalid pagination gracefully
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(userQueryService, times(1)).getUsers(any(UserCriteria.class), any(Pageable.class));
    }
}
