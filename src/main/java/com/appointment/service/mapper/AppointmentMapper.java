package com.appointment.service.mapper;

import com.appointment.entity.Appointment;
import com.appointment.service.dto.response.AppointmentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "customerName", expression = "java(appointment.getCustomer().getFirstName() + \" \" + appointment.getCustomer().getLastName())")
    @Mapping(target = "vehicleId", source = "vehicle.id")
    @Mapping(target = "vehicleLicensePlate", source = "vehicle.licensePlate")
    @Mapping(target = "technicianId", source = "technician.id")
    @Mapping(target = "technicianName", source = "technician.name")
    @Mapping(target = "serviceBayId", source = "serviceBay.id")
    @Mapping(target = "serviceBayName", source = "serviceBay.name")
    @Mapping(target = "dealershipId", source = "dealership.id")
    @Mapping(target = "dealershipName", source = "dealership.name")
    @Mapping(target = "serviceTypeId", source = "serviceType.id")
    @Mapping(target = "serviceTypeName", source = "serviceType.name")
    AppointmentResponseDto toResponseDto(Appointment appointment);
}
