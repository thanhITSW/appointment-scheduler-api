package com.appointment.service;

import com.appointment.service.dto.request.CreateSkillRequestDto;
import com.appointment.service.dto.response.SkillResponseDto;

import java.util.List;

public interface SkillService {

    List<SkillResponseDto> getSkills();

    SkillResponseDto createSkill(CreateSkillRequestDto request);
}
