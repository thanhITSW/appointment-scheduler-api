package com.appointment.controller.privates;

import com.appointment.service.UserQueryService;
import com.appointment.service.UserService;
import com.appointment.service.criteria.UserCriteria;
import com.appointment.service.dto.request.ChangePasswordRequestDto;
import com.appointment.service.dto.request.CreateUserRequestDto;
import com.appointment.service.dto.request.DeleteUserRequestDto;
import com.appointment.service.dto.request.UpdateUserRequestDto;
import com.appointment.service.dto.response.ErrorResponse;
import com.appointment.service.dto.response.UserResponseDto;
import com.appointment.utils.ResponseUtils;
import com.appointment.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users (Admin only)")
@SecurityRequirement(name = "bearerAuth")
public class UserManagementPrivateController {

    private final UserService userService;
    private final UserQueryService userQueryService;

    @GetMapping
    @Operation(summary = "Get Users with Pagination", description = "Get paginated list of users with filtering options. Requires Admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<UserResponseDto>> getUsers(
            @ParameterObject UserCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("Getting users with criteria: {}", criteria);
        final Page<UserResponseDto> page = userQueryService.getUsers(criteria, pageable);
        HttpHeaders headers = ResponseUtils.generatePaginationHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping
    @Operation(summary = "Create New User",
            description = "Create a new user with employeeId, email, fullName, password, and role. Requires Admin role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid user data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = "{\"messageCode\": \"error.employee-id.duplicated\", \"message\": \"Employee ID already exists\"}"))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> createUser(@Valid @RequestBody CreateUserRequestDto request) {
        userService.createNewUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/delete-confirm")
    @Operation(summary = "Delete User with admin confirmation")
    public ResponseEntity<Void> deleteUserWithAdminConfirmation(
            @PathVariable Long id,
            @Valid @RequestBody DeleteUserRequestDto request) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUserWithAdminConfirmation(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/change-password")
    @Operation(summary = "Change User Password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequestDto request) {
        Long userId = SecurityUtils.getCurrentUserIdOrThrow();
        userService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/delete")
    @Operation(summary = "Delete User")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{id}/update")
    @Operation(summary = "Update User")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        log.info("Update user with ID: {}", id);
        userService.updateUserByUserId(id, updateUserRequestDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
