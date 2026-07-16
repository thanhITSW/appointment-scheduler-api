package com.appointment.service;

import com.appointment.service.dto.request.CreateCustomerRequestDto;
import com.appointment.service.dto.request.UpdateCustomerRequestDto;
import com.appointment.service.dto.response.CustomerResponseDto;

import java.util.List;

public interface CustomerService {

    List<CustomerResponseDto> searchCustomers(String keyword);

    CustomerResponseDto createCustomer(CreateCustomerRequestDto request);

    CustomerResponseDto getCustomerById(Long customerId);

    CustomerResponseDto updateCustomer(Long customerId, UpdateCustomerRequestDto request);
}
