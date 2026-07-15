package com.appointment.controller.privates;

import com.appointment.service.TechnicianService;
import com.appointment.service.dto.request.CreateTechnicianRequestDto;
import com.appointment.service.dto.request.UpdateTechnicianRequestDto;
import com.appointment.service.dto.request.UpdateTechnicianSkillsRequestDto;
import com.appointment.service.dto.response.TechnicianResponseDto;
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
@RequestMapping("/api/v1/private/technicians")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private Technicians", description = "Manage technicians and their skills")
@SecurityRequirement(name = "bearerAuth")
public class TechnicianPrivateController {

    private final TechnicianService technicianService;

    @GetMapping
    @Operation(summary = "List technicians")
    public ResponseEntity<List<TechnicianResponseDto>> getTechnicians() {
        return ResponseEntity.ok(technicianService.getTechnicians());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get technician")
    public ResponseEntity<TechnicianResponseDto> getTechnician(@PathVariable Long id) {
        return ResponseEntity.ok(technicianService.getTechnicianById(id));
    }

    @PostMapping
    @Operation(summary = "Create technician")
    public ResponseEntity<TechnicianResponseDto> createTechnician(
            @Valid @RequestBody CreateTechnicianRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(technicianService.createTechnician(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update technician")
    public ResponseEntity<TechnicianResponseDto> updateTechnician(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTechnicianRequestDto request) {
        return ResponseEntity.ok(technicianService.updateTechnician(id, request));
    }

    @PutMapping("/{id}/skills")
    @Operation(summary = "Set technician skills")
    public ResponseEntity<TechnicianResponseDto> updateTechnicianSkills(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTechnicianSkillsRequestDto request) {
        return ResponseEntity.ok(technicianService.updateTechnicianSkills(id, request));
    }
}
