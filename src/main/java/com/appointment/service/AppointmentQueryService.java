package com.appointment.service;

import com.appointment.enumeration.AppointmentStatus;
import com.appointment.service.dto.response.AppointmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AppointmentQueryService {

    Page<AppointmentResponseDto> search(LocalDate date, Long customerId, AppointmentStatus status, Pageable pageable);

    AppointmentResponseDto getById(Long id);
}
