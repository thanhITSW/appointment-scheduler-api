package com.appointment.controller.publics;

import com.appointment.enumeration.AppointmentStatus;
import com.appointment.service.AppointmentQueryService;
import com.appointment.service.AppointmentService;
import com.appointment.service.dto.request.CheckAvailabilityRequestDto;
import com.appointment.service.dto.request.CreateAppointmentRequestDto;
import com.appointment.service.dto.response.AppointmentResponseDto;
import com.appointment.service.dto.response.AvailabilityResponseDto;
import com.appointment.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public/appointments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Appointments", description = "Public APIs for appointment booking and search")
public class AppointmentPublicController {

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

    @PostMapping("/check-availability")
    @Operation(summary = "Check availability", description = "Check if a technician and service bay are available")
    public ResponseEntity<AvailabilityResponseDto> checkAvailability(
            @Valid @RequestBody CheckAvailabilityRequestDto request) {
        return ResponseEntity.ok(appointmentService.checkAvailability(request));
    }

    @PostMapping
    @Operation(summary = "Create appointment", description = "Create a new appointment with locked resource allocation")
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @Valid @RequestBody CreateAppointmentRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.createAppointment(request));
    }
}
