package com.appointment.service.mapper;

import com.appointment.entity.Customer;
import com.appointment.service.dto.request.CreateCustomerRequestDto;
import com.appointment.service.dto.response.CustomerResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {

    CustomerResponseDto toResponseDto(Customer customer);

    Customer toEntity(CreateCustomerRequestDto request);
}
