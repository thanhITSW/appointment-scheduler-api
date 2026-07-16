package com.appointment.service.dto.request;

import com.appointment.enumeration.TechnicianStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTechnicianRequestDto {

    @NotBlank
    private String name;

    @NotBlank
    private String employeeCode;

    @NotNull
    private TechnicianStatus status;

    @NotNull
    private Long dealershipId;

    private List<Long> skillIds;
}
