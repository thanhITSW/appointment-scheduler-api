package com.appointment.service.dto.request;

import jakarta.validation.constraints.Min;
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
public class UpdateServiceTypeRequestDto {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer durationMinutes;

    private List<Long> requiredSkillIds;
}
