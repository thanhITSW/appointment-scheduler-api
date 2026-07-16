package com.appointment.controller.privates;

import com.appointment.service.ServiceBayService;
import com.appointment.service.dto.request.CreateServiceBayRequestDto;
import com.appointment.service.dto.request.UpdateServiceBayRequestDto;
import com.appointment.service.dto.response.ServiceBayResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private/service-bays")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private Service Bays", description = "Manage service bays")
@SecurityRequirement(name = "bearerAuth")
public class ServiceBayPrivateController {

    private final ServiceBayService serviceBayService;

    @GetMapping
    @Operation(summary = "List service bays")
    public ResponseEntity<List<ServiceBayResponseDto>> getServiceBays() {
        return ResponseEntity.ok(serviceBayService.getServiceBays());
    }

    @PostMapping
    @Operation(summary = "Create service bay")
    public ResponseEntity<ServiceBayResponseDto> createServiceBay(
            @Valid @RequestBody CreateServiceBayRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceBayService.createServiceBay(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update service bay")
    public ResponseEntity<ServiceBayResponseDto> updateServiceBay(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceBayRequestDto request) {
        return ResponseEntity.ok(serviceBayService.updateServiceBay(id, request));
    }
}
