package com.appointment.service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehicleRequestDto {

    @NotBlank
    private String vin;

    @NotBlank
    private String licensePlate;

    @NotBlank
    private String make;

    @NotBlank
    private String model;

    @NotNull
    private Integer year;
}
