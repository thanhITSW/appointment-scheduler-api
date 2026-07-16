package com.appointment.service;

import com.appointment.service.dto.request.CreateServiceTypeRequestDto;
import com.appointment.service.dto.request.UpdateServiceTypeRequestDto;
import com.appointment.service.dto.response.DealershipResponseDto;
import com.appointment.service.dto.response.ServiceTypeResponseDto;

import java.util.List;

public interface MasterDataService {

    List<ServiceTypeResponseDto> getServiceTypes();

    List<DealershipResponseDto> getDealerships();

    ServiceTypeResponseDto createServiceType(CreateServiceTypeRequestDto request);

    ServiceTypeResponseDto updateServiceType(Long id, UpdateServiceTypeRequestDto request);
}
