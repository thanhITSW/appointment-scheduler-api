package com.appointment.controller.privates;

import com.appointment.service.MasterDataService;
import com.appointment.service.dto.request.CreateServiceTypeRequestDto;
import com.appointment.service.dto.request.UpdateServiceTypeRequestDto;
import com.appointment.service.dto.response.ServiceTypeResponseDto;
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
@RequestMapping("/api/v1/private/service-types")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private Service Types", description = "Manage service types and required skills")
@SecurityRequirement(name = "bearerAuth")
public class ServiceTypePrivateController {

    private final MasterDataService masterDataService;

    @GetMapping
    @Operation(summary = "List service types")
    public ResponseEntity<List<ServiceTypeResponseDto>> getServiceTypes() {
        return ResponseEntity.ok(masterDataService.getServiceTypes());
    }

    @PostMapping
    @Operation(summary = "Create service type")
    public ResponseEntity<ServiceTypeResponseDto> createServiceType(
            @Valid @RequestBody CreateServiceTypeRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterDataService.createServiceType(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update service type")
    public ResponseEntity<ServiceTypeResponseDto> updateServiceType(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceTypeRequestDto request) {
        return ResponseEntity.ok(masterDataService.updateServiceType(id, request));
    }
}
