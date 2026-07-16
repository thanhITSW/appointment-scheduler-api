package com.appointment.service.impl;

import com.appointment.entity.Customer;
import com.appointment.entity.Vehicle;
import com.appointment.exception.DataNotfoundException;
import com.appointment.repository.CustomerRepository;
import com.appointment.repository.VehicleRepository;
import com.appointment.service.VehicleService;
import com.appointment.service.dto.request.CreateVehicleRequestDto;
import com.appointment.service.dto.response.VehicleResponseDto;
import com.appointment.service.mapper.VehicleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.appointment.constant.ErrorCodeConstant.ERR_CUSTOMER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class VehicleServiceImpl implements VehicleService {

    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    @Override
    public List<VehicleResponseDto> getCustomerVehicles(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new DataNotfoundException(ERR_CUSTOMER_NOT_FOUND);
        }
        return vehicleRepository.findByCustomerIdOrderByIdAsc(customerId).stream()
                .map(vehicleMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public VehicleResponseDto createVehicle(Long customerId, CreateVehicleRequestDto request) {
        log.info("Creating vehicle for customerId={}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotfoundException(ERR_CUSTOMER_NOT_FOUND));

        Vehicle vehicle = vehicleMapper.toEntity(request);
        vehicle.setCustomer(customer);
        Vehicle saved = vehicleRepository.save(vehicle);
        return vehicleMapper.toResponseDto(saved);
    }
}
