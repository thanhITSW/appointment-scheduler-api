package com.appointment.service;

import com.appointment.service.dto.request.CreateTechnicianRequestDto;
import com.appointment.service.dto.request.UpdateTechnicianRequestDto;
import com.appointment.service.dto.request.UpdateTechnicianSkillsRequestDto;
import com.appointment.service.dto.response.TechnicianResponseDto;

import java.util.List;

public interface TechnicianService {

    List<TechnicianResponseDto> getTechnicians();

    TechnicianResponseDto getTechnicianById(Long id);

    TechnicianResponseDto createTechnician(CreateTechnicianRequestDto request);

    TechnicianResponseDto updateTechnician(Long id, UpdateTechnicianRequestDto request);

    TechnicianResponseDto updateTechnicianSkills(Long id, UpdateTechnicianSkillsRequestDto request);
}
