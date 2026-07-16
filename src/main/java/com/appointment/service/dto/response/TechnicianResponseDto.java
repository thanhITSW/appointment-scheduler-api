package com.appointment.service.dto.response;

import com.appointment.enumeration.TechnicianStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianResponseDto {

    private Long id;
    private String name;
    private String employeeCode;
    private TechnicianStatus status;
    private Long dealershipId;
    private String dealershipName;
    private List<Long> skillIds;
    private List<String> skillCodes;
}
