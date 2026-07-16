package com.appointment.service;

import com.appointment.service.dto.request.CreateVehicleRequestDto;
import com.appointment.service.dto.response.VehicleResponseDto;

import java.util.List;

public interface VehicleService {

    List<VehicleResponseDto> getCustomerVehicles(Long customerId);

    VehicleResponseDto createVehicle(Long customerId, CreateVehicleRequestDto request);
}
