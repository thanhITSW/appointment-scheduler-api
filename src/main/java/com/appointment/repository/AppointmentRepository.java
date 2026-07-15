package com.appointment.repository;

import com.appointment.entity.Appointment;
import com.appointment.enumeration.AppointmentStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    @EntityGraph(attributePaths = {
            "customer", "vehicle", "technician", "serviceBay", "dealership", "serviceType"
    })
    Optional<Appointment> findWithDetailsById(Long id);

    boolean existsByTechnicianIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Long technicianId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses,
            LocalTime endTime,
            LocalTime startTime);

    boolean existsByServiceBayIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Long serviceBayId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses,
            LocalTime endTime,
            LocalTime startTime);

    boolean existsByTechnicianIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
            Long technicianId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses,
            LocalTime endTime,
            LocalTime startTime,
            Long id);

    boolean existsByServiceBayIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
            Long serviceBayId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses,
            LocalTime endTime,
            LocalTime startTime,
            Long id);
}
