package com.appointment.controller.privates;

import com.appointment.service.SkillService;
import com.appointment.service.dto.request.CreateSkillRequestDto;
import com.appointment.service.dto.response.SkillResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private/skills")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private Skills", description = "Manage technician/service skills")
@SecurityRequirement(name = "bearerAuth")
public class SkillPrivateController {

    private final SkillService skillService;

    @GetMapping
    @Operation(summary = "List skills")
    public ResponseEntity<List<SkillResponseDto>> getSkills() {
        return ResponseEntity.ok(skillService.getSkills());
    }

    @PostMapping
    @Operation(summary = "Create skill")
    public ResponseEntity<SkillResponseDto> createSkill(@Valid @RequestBody CreateSkillRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(request));
    }
}
