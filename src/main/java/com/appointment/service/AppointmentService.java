package com.appointment.service;

import com.appointment.enumeration.AppointmentStatus;
import com.appointment.service.dto.request.CheckAvailabilityRequestDto;
import com.appointment.service.dto.request.CreateAppointmentRequestDto;
import com.appointment.service.dto.request.RescheduleAppointmentRequestDto;
import com.appointment.service.dto.response.AppointmentResponseDto;
import com.appointment.service.dto.response.AvailabilityResponseDto;

public interface AppointmentService {

    AvailabilityResponseDto checkAvailability(CheckAvailabilityRequestDto request);

    AppointmentResponseDto createAppointment(CreateAppointmentRequestDto request);

    AppointmentResponseDto cancelAppointment(Long id);

    AppointmentResponseDto updateAppointmentStatus(Long id, AppointmentStatus status);

    AppointmentResponseDto rescheduleAppointment(Long id, RescheduleAppointmentRequestDto request);
}
