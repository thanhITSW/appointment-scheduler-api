package com.appointment.service.impl;

import com.appointment.entity.Appointment;
import com.appointment.enumeration.AppointmentStatus;
import com.appointment.exception.DataNotfoundException;
import com.appointment.repository.AppointmentRepository;
import com.appointment.service.AppointmentQueryService;
import com.appointment.service.dto.response.AppointmentResponseDto;
import com.appointment.service.mapper.AppointmentMapper;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.appointment.constant.ErrorCodeConstant.ERR_APPOINTMENT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AppointmentQueryServiceImpl implements AppointmentQueryService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public Page<AppointmentResponseDto> search(LocalDate date, Long customerId, AppointmentStatus status, Pageable pageable) {
        Specification<Appointment> specification = (root, query, cb) -> {
            // Fetch associations so mapping works with open-in-view=false
            if (Appointment.class.equals(query.getResultType())) {
                root.fetch("customer", JoinType.LEFT);
                root.fetch("vehicle", JoinType.LEFT);
                root.fetch("technician", JoinType.LEFT);
                root.fetch("serviceBay", JoinType.LEFT);
                root.fetch("dealership", JoinType.LEFT);
                root.fetch("serviceType", JoinType.LEFT);
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();
            if (date != null) {
                predicates.add(cb.equal(root.get("appointmentDate"), date));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), customerId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return appointmentRepository.findAll(specification, pageable)
                .map(appointmentMapper::toResponseDto);
    }

    @Override
    public AppointmentResponseDto getById(Long id) {
        Appointment appointment = appointmentRepository.findWithDetailsById(id)
                .orElseThrow(() -> new DataNotfoundException(ERR_APPOINTMENT_NOT_FOUND));
        return appointmentMapper.toResponseDto(appointment);
    }
}
