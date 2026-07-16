package com.appointment.service.dto.response;

import com.appointment.enumeration.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponseDto {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long vehicleId;
    private String vehicleLicensePlate;
    private Long technicianId;
    private String technicianName;
    private Long serviceBayId;
    private String serviceBayName;
    private Long dealershipId;
    private String dealershipName;
    private Long serviceTypeId;
    private String serviceTypeName;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private AppointmentStatus status;
}
