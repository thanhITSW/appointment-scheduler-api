package com.appointment.service.mapper;

import com.appointment.entity.Vehicle;
import com.appointment.service.dto.request.CreateVehicleRequestDto;
import com.appointment.service.dto.response.VehicleResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VehicleMapper {

    @Mapping(target = "customerId", source = "customer.id")
    VehicleResponseDto toResponseDto(Vehicle vehicle);

    Vehicle toEntity(CreateVehicleRequestDto request);
}
