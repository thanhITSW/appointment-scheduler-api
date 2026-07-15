package com.appointment.service.mapper;

import com.appointment.entity.Dealership;
import com.appointment.entity.ServiceBay;
import com.appointment.entity.ServiceType;
import com.appointment.entity.Skill;
import com.appointment.entity.Technician;
import com.appointment.service.dto.response.DealershipResponseDto;
import com.appointment.service.dto.response.ServiceBayResponseDto;
import com.appointment.service.dto.response.ServiceTypeResponseDto;
import com.appointment.service.dto.response.SkillResponseDto;
import com.appointment.service.dto.response.TechnicianResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MasterDataMapper {

    SkillResponseDto toSkillResponseDto(Skill skill);

    ServiceBayResponseDto toServiceBayResponseDto(ServiceBay serviceBay);

    DealershipResponseDto toDealershipResponseDto(Dealership dealership);

    @Mapping(target = "requiredSkillIds", expression = "java(toSkillIds(serviceType.getRequiredSkills()))")
    @Mapping(target = "requiredSkillCodes", expression = "java(toSkillCodes(serviceType.getRequiredSkills()))")
    ServiceTypeResponseDto toServiceTypeResponseDto(ServiceType serviceType);

    @Mapping(target = "skillIds", expression = "java(toSkillIds(technician.getSkills()))")
    @Mapping(target = "skillCodes", expression = "java(toSkillCodes(technician.getSkills()))")
    TechnicianResponseDto toTechnicianResponseDto(Technician technician);

    default List<Long> toSkillIds(Set<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptyList();
        }
        return skills.stream().map(Skill::getId).sorted().collect(Collectors.toList());
    }

    default List<String> toSkillCodes(Set<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptyList();
        }
        return skills.stream().map(Skill::getCode).sorted().collect(Collectors.toList());
    }
}
