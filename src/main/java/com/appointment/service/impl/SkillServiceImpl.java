package com.appointment.service.impl;

import com.appointment.entity.Skill;
import com.appointment.exception.DataConflictException;
import com.appointment.repository.SkillRepository;
import com.appointment.service.SkillService;
import com.appointment.service.dto.request.CreateSkillRequestDto;
import com.appointment.service.dto.response.SkillResponseDto;
import com.appointment.service.mapper.MasterDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.appointment.constant.ErrorCodeConstant.ERR_SKILL_CODE_DUPLICATED;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final MasterDataMapper masterDataMapper;

    @Override
    public List<SkillResponseDto> getSkills() {
        return skillRepository.findAll().stream()
                .map(masterDataMapper::toSkillResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public SkillResponseDto createSkill(CreateSkillRequestDto request) {
        String code = request.getCode().trim().toUpperCase();
        if (skillRepository.existsByCode(code)) {
            throw new DataConflictException(ERR_SKILL_CODE_DUPLICATED);
        }
        Skill skill = Skill.builder()
                .code(code)
                .name(request.getName().trim())
                .build();
        return masterDataMapper.toSkillResponseDto(skillRepository.save(skill));
    }
}
