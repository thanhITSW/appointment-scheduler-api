package com.appointment.service.impl;

import com.appointment.entity.ServiceType;
import com.appointment.entity.Skill;
import com.appointment.exception.DataNotfoundException;
import com.appointment.repository.DealershipRepository;
import com.appointment.repository.ServiceTypeRepository;
import com.appointment.repository.SkillRepository;
import com.appointment.service.MasterDataService;
import com.appointment.service.dto.request.CreateServiceTypeRequestDto;
import com.appointment.service.dto.request.UpdateServiceTypeRequestDto;
import com.appointment.service.dto.response.DealershipResponseDto;
import com.appointment.service.dto.response.ServiceTypeResponseDto;
import com.appointment.service.mapper.MasterDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.appointment.constant.ErrorCodeConstant.ERR_SERVICE_TYPE_NOT_FOUND;
import static com.appointment.constant.ErrorCodeConstant.ERR_SKILL_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MasterDataServiceImpl implements MasterDataService {

    private final ServiceTypeRepository serviceTypeRepository;
    private final DealershipRepository dealershipRepository;
    private final SkillRepository skillRepository;
    private final MasterDataMapper masterDataMapper;

    @Override
    public List<ServiceTypeResponseDto> getServiceTypes() {
        return serviceTypeRepository.findAllByOrderByIdAsc().stream()
                .map(masterDataMapper::toServiceTypeResponseDto)
                .toList();
    }

    @Override
    public List<DealershipResponseDto> getDealerships() {
        return dealershipRepository.findAll().stream()
                .map(masterDataMapper::toDealershipResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ServiceTypeResponseDto createServiceType(CreateServiceTypeRequestDto request) {
        ServiceType serviceType = ServiceType.builder()
                .name(request.getName().trim())
                .durationMinutes(request.getDurationMinutes())
                .requiredSkills(resolveSkills(request.getRequiredSkillIds()))
                .build();
        return masterDataMapper.toServiceTypeResponseDto(serviceTypeRepository.save(serviceType));
    }

    @Override
    @Transactional
    public ServiceTypeResponseDto updateServiceType(Long id, UpdateServiceTypeRequestDto request) {
        ServiceType serviceType = serviceTypeRepository.findWithSkillsById(id)
                .orElseThrow(() -> new DataNotfoundException(ERR_SERVICE_TYPE_NOT_FOUND));
        serviceType.setName(request.getName().trim());
        serviceType.setDurationMinutes(request.getDurationMinutes());
        serviceType.setRequiredSkills(resolveSkills(request.getRequiredSkillIds()));
        return masterDataMapper.toServiceTypeResponseDto(serviceTypeRepository.save(serviceType));
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
