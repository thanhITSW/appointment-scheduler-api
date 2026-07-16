package com.appointment.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeResponseDto {

    private Long id;
    private String name;
    private Integer durationMinutes;
    private List<Long> requiredSkillIds;
    private List<String> requiredSkillCodes;
}
