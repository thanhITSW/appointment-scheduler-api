package com.appointment.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponseDto {

    private Long id;
    private Long customerId;
    private String vin;
    private String licensePlate;
    private String make;
    private String model;
    private Integer year;
}
