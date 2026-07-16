package com.appointment.service;

import com.appointment.service.dto.request.CreateServiceBayRequestDto;
import com.appointment.service.dto.request.UpdateServiceBayRequestDto;
import com.appointment.service.dto.response.ServiceBayResponseDto;

import java.util.List;

public interface ServiceBayService {

    List<ServiceBayResponseDto> getServiceBays();

    ServiceBayResponseDto createServiceBay(CreateServiceBayRequestDto request);

    ServiceBayResponseDto updateServiceBay(Long id, UpdateServiceBayRequestDto request);
}
