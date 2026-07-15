package com.appointment.service.impl;

import com.appointment.entity.Customer;
import com.appointment.exception.DataNotfoundException;
import com.appointment.repository.CustomerRepository;
import com.appointment.service.CustomerService;
import com.appointment.service.dto.request.CreateCustomerRequestDto;
import com.appointment.service.dto.request.UpdateCustomerRequestDto;
import com.appointment.service.dto.response.CustomerResponseDto;
import com.appointment.service.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.appointment.constant.ErrorCodeConstant.ERR_CUSTOMER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerResponseDto> searchCustomers(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return customerRepository.findAll().stream()
                    .map(customerMapper::toResponseDto)
                    .toList();
        }
        return customerRepository.searchByKeyword(keyword.trim()).stream()
                .map(customerMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public CustomerResponseDto createCustomer(CreateCustomerRequestDto request) {
        log.info("Creating customer with phone: {}", request.getPhone());
        Customer customer = customerMapper.toEntity(request);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toResponseDto(saved);
    }

    @Override
    public CustomerResponseDto getCustomerById(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotfoundException(ERR_CUSTOMER_NOT_FOUND));
        return customerMapper.toResponseDto(customer);
    }

    @Override
    @Transactional
    public CustomerResponseDto updateCustomer(Long customerId, UpdateCustomerRequestDto request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new DataNotfoundException(ERR_CUSTOMER_NOT_FOUND));
        customer.setFirstName(request.getFirstName().trim());
        customer.setLastName(request.getLastName().trim());
        customer.setPhone(request.getPhone().trim());
        customer.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        return customerMapper.toResponseDto(customerRepository.save(customer));
    }
}
