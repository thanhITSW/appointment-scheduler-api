package com.appointment.service.impl;

import com.appointment.entity.Dealership;
import com.appointment.entity.ServiceBay;
import com.appointment.exception.DataNotfoundException;
import com.appointment.repository.DealershipRepository;
import com.appointment.repository.ServiceBayRepository;
import com.appointment.service.ServiceBayService;
import com.appointment.service.dto.request.CreateServiceBayRequestDto;
import com.appointment.service.dto.request.UpdateServiceBayRequestDto;
import com.appointment.service.dto.response.ServiceBayResponseDto;
import com.appointment.service.mapper.MasterDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.appointment.constant.ErrorCodeConstant.ERR_DEALERSHIP_NOT_FOUND;
import static com.appointment.constant.ErrorCodeConstant.ERR_SERVICE_BAY_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ServiceBayServiceImpl implements ServiceBayService {

    private final ServiceBayRepository serviceBayRepository;
    private final DealershipRepository dealershipRepository;
    private final MasterDataMapper masterDataMapper;

    @Override
    public List<ServiceBayResponseDto> getServiceBays() {
        return serviceBayRepository.findAll().stream()
                .map(masterDataMapper::toServiceBayResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ServiceBayResponseDto createServiceBay(CreateServiceBayRequestDto request) {
        ServiceBay serviceBay = ServiceBay.builder()
                .name(request.getName().trim())
                .status(request.getStatus())
                .dealership(requireDealership(request.getDealershipId()))
                .build();
        return masterDataMapper.toServiceBayResponseDto(serviceBayRepository.save(serviceBay));
    }

    @Override
    @Transactional
    public ServiceBayResponseDto updateServiceBay(Long id, UpdateServiceBayRequestDto request) {
        ServiceBay serviceBay = serviceBayRepository.findById(id)
                .orElseThrow(() -> new DataNotfoundException(ERR_SERVICE_BAY_NOT_FOUND));
        serviceBay.setName(request.getName().trim());
        serviceBay.setStatus(request.getStatus());
        serviceBay.setDealership(requireDealership(request.getDealershipId()));
        return masterDataMapper.toServiceBayResponseDto(serviceBayRepository.save(serviceBay));
    }

    private Dealership requireDealership(Long dealershipId) {
        return dealershipRepository.findById(dealershipId)
                .orElseThrow(() -> new DataNotfoundException(ERR_DEALERSHIP_NOT_FOUND));
    }
}
