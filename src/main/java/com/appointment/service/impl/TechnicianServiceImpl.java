package com.appointment.service.impl;

import com.appointment.entity.Skill;
import com.appointment.entity.Technician;
import com.appointment.exception.DataConflictException;
import com.appointment.exception.DataNotfoundException;
import com.appointment.repository.SkillRepository;
import com.appointment.repository.TechnicianRepository;
import com.appointment.service.TechnicianService;
import com.appointment.service.dto.request.CreateTechnicianRequestDto;
import com.appointment.service.dto.request.UpdateTechnicianRequestDto;
import com.appointment.service.dto.request.UpdateTechnicianSkillsRequestDto;
import com.appointment.service.dto.response.TechnicianResponseDto;
import com.appointment.service.mapper.MasterDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.appointment.constant.ErrorCodeConstant.ERR_SKILL_NOT_FOUND;
import static com.appointment.constant.ErrorCodeConstant.ERR_TECHNICIAN_EMPLOYEE_CODE_DUPLICATED;
import static com.appointment.constant.ErrorCodeConstant.ERR_TECHNICIAN_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TechnicianServiceImpl implements TechnicianService {

    private final TechnicianRepository technicianRepository;
    private final SkillRepository skillRepository;
    private final MasterDataMapper masterDataMapper;

    @Override
    public List<TechnicianResponseDto> getTechnicians() {
        return technicianRepository.findAllByOrderByIdAsc().stream()
                .map(masterDataMapper::toTechnicianResponseDto)
                .toList();
    }

    @Override
    public TechnicianResponseDto getTechnicianById(Long id) {
        Technician technician = technicianRepository.findWithSkillsById(id)
                .orElseThrow(() -> new DataNotfoundException(ERR_TECHNICIAN_NOT_FOUND));
        return masterDataMapper.toTechnicianResponseDto(technician);
    }

    @Override
    @Transactional
    public TechnicianResponseDto createTechnician(CreateTechnicianRequestDto request) {
        if (technicianRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new DataConflictException(ERR_TECHNICIAN_EMPLOYEE_CODE_DUPLICATED);
        }
        Technician technician = Technician.builder()
                .name(request.getName().trim())
                .employeeCode(request.getEmployeeCode().trim())
                .status(request.getStatus())
                .skills(resolveSkills(request.getSkillIds()))
                .build();
        return masterDataMapper.toTechnicianResponseDto(technicianRepository.save(technician));
    }

    @Override
    @Transactional
    public TechnicianResponseDto updateTechnician(Long id, UpdateTechnicianRequestDto request) {
        Technician technician = technicianRepository.findWithSkillsById(id)
                .orElseThrow(() -> new DataNotfoundException(ERR_TECHNICIAN_NOT_FOUND));
        if (technicianRepository.existsByEmployeeCodeAndIdNot(request.getEmployeeCode(), id)) {
            throw new DataConflictException(ERR_TECHNICIAN_EMPLOYEE_CODE_DUPLICATED);
        }
        technician.setName(request.getName().trim());
        technician.setEmployeeCode(request.getEmployeeCode().trim());
        technician.setStatus(request.getStatus());
        return masterDataMapper.toTechnicianResponseDto(technicianRepository.save(technician));
    }

    @Override
    @Transactional
    public TechnicianResponseDto updateTechnicianSkills(Long id, UpdateTechnicianSkillsRequestDto request) {
        Technician technician = technicianRepository.findWithSkillsById(id)
                .orElseThrow(() -> new DataNotfoundException(ERR_TECHNICIAN_NOT_FOUND));
        technician.setSkills(resolveSkills(request.getSkillIds()));
        return masterDataMapper.toTechnicianResponseDto(technicianRepository.save(technician));
    }

    private Set<Skill> resolveSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Skill> skills = skillRepository.findByIdIn(skillIds);
        if (skills.size() != skillIds.stream().distinct().count()) {
            throw new DataNotfoundException(ERR_SKILL_NOT_FOUND);
        }
        return new HashSet<>(skills);
    }
}
