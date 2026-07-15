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
public class CreateAppointmentRequestDto {

    @NotNull
    private Long customerId;

    @NotNull
    private Long vehicleId;

    @NotNull
    private Long serviceTypeId;

    @NotNull
    private Long dealershipId;

    @NotNull
    private LocalDate appointmentDate;

    @NotNull
    private LocalTime startTime;
}
