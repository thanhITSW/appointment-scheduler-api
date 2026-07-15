package com.appointment.service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckAvailabilityRequestDto {

    @NotNull
    private Long dealershipId;

    @NotNull
    private Long serviceTypeId;

    @NotNull
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime startTime;
}
