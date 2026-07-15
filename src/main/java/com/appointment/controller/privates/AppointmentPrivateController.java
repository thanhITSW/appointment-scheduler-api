package com.appointment.controller.privates;

import com.appointment.enumeration.AppointmentStatus;
import com.appointment.service.AppointmentQueryService;
import com.appointment.service.AppointmentService;
import com.appointment.service.dto.request.RescheduleAppointmentRequestDto;
import com.appointment.service.dto.request.UpdateAppointmentStatusRequestDto;
import com.appointment.service.dto.response.AppointmentResponseDto;
import com.appointment.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/private/appointments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private Appointments", description = "Staff APIs for appointment lifecycle management")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentPrivateController {

    private final AppointmentService appointmentService;
    private final AppointmentQueryService appointmentQueryService;

    @GetMapping
    @Operation(summary = "Search appointments", description = "Search appointments by date, customer, and status")
    public ResponseEntity<List<AppointmentResponseDto>> searchAppointments(
            @Parameter(description = "Appointment date filter")
            @RequestParam(required = false) LocalDate date,
            @Parameter(description = "Customer ID filter")
            @RequestParam(required = false) Long customerId,
            @Parameter(description = "Status filter")
            @RequestParam(required = false) AppointmentStatus status,
            @ParameterObject
            @PageableDefault(size = 20, sort = "appointmentDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<AppointmentResponseDto> page = appointmentQueryService.search(date, customerId, status, pageable);
        HttpHeaders headers = ResponseUtils.generatePaginationHeaders(
                ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment", description = "Get appointment details by ID")
    public ResponseEntity<AppointmentResponseDto> getAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentQueryService.getById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Transition appointment status along allowed paths")
    public ResponseEntity<AppointmentResponseDto> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentStatusRequestDto request) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, request.getStatus()));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel appointment", description = "Cancel a PENDING or CONFIRMED appointment")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    @PutMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule appointment", description = "Reschedule with technician/bay reallocation under locks")
    public ResponseEntity<AppointmentResponseDto> rescheduleAppointment(
            @PathVariable Long id,
            @Valid @RequestBody RescheduleAppointmentRequestDto request) {
        return ResponseEntity.ok(appointmentService.rescheduleAppointment(id, request));
    }
}
