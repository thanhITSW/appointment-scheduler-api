package com.appointment.service.impl;

import com.appointment.entity.Appointment;
import com.appointment.entity.Customer;
import com.appointment.entity.Dealership;
import com.appointment.entity.ServiceBay;
import com.appointment.entity.ServiceType;
import com.appointment.entity.Skill;
import com.appointment.entity.Technician;
import com.appointment.entity.Vehicle;
import com.appointment.enumeration.AppointmentStatus;
import com.appointment.enumeration.ServiceBayStatus;
import com.appointment.enumeration.TechnicianStatus;
import com.appointment.exception.BadRequestException;
import com.appointment.exception.DataConflictException;
import com.appointment.exception.DataNotfoundException;
import com.appointment.repository.AppointmentRepository;
import com.appointment.repository.CustomerRepository;
import com.appointment.repository.DealershipRepository;
import com.appointment.repository.ServiceBayRepository;
import com.appointment.repository.ServiceTypeRepository;
import com.appointment.repository.TechnicianRepository;
import com.appointment.repository.VehicleRepository;
import com.appointment.service.AppointmentService;
import com.appointment.service.dto.request.CheckAvailabilityRequestDto;
import com.appointment.service.dto.request.CreateAppointmentRequestDto;
import com.appointment.service.dto.request.RescheduleAppointmentRequestDto;
import com.appointment.service.dto.response.AppointmentResponseDto;
import com.appointment.service.dto.response.AvailabilityResponseDto;
import com.appointment.service.mapper.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.appointment.constant.ErrorCodeConstant.ERR_APPOINTMENT_IN_PAST;
import static com.appointment.constant.ErrorCodeConstant.ERR_APPOINTMENT_INVALID_STATUS;
import static com.appointment.constant.ErrorCodeConstant.ERR_APPOINTMENT_NOT_FOUND;
import static com.appointment.constant.ErrorCodeConstant.ERR_CUSTOMER_NOT_FOUND;
import static com.appointment.constant.ErrorCodeConstant.ERR_DEALERSHIP_NOT_FOUND;
import static com.appointment.constant.ErrorCodeConstant.ERR_NO_AVAILABLE_SERVICE_BAY;
import static com.appointment.constant.ErrorCodeConstant.ERR_NO_AVAILABLE_TECHNICIAN;
import static com.appointment.constant.ErrorCodeConstant.ERR_SERVICE_TYPE_NOT_FOUND;
import static com.appointment.constant.ErrorCodeConstant.ERR_VEHICLE_NOT_FOUND;
import static com.appointment.constant.ErrorCodeConstant.ERR_VEHICLE_NOT_OWNED_BY_CUSTOMER;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {

    private static final Set<AppointmentStatus> BLOCKING_STATUSES = Set.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.CONFIRMED
    );

    /**
     * Past checks use {@link ZoneId#systemDefault()} so "now" matches the JVM/host zone.
     */
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final DealershipRepository dealershipRepository;
    private final TechnicianRepository technicianRepository;
    private final ServiceBayRepository serviceBayRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public AvailabilityResponseDto checkAvailability(CheckAvailabilityRequestDto request) {
        validateNotPast(request.getAppointmentDate(), request.getStartTime());

        ServiceType serviceType = serviceTypeRepository.findWithSkillsById(request.getServiceTypeId())
                .orElseThrow(() -> new DataNotfoundException(ERR_SERVICE_TYPE_NOT_FOUND));
        dealershipRepository.findById(request.getDealershipId())
                .orElseThrow(() -> new DataNotfoundException(ERR_DEALERSHIP_NOT_FOUND));

        LocalTime endTime = request.getStartTime().plusMinutes(serviceType.getDurationMinutes());
        Long dealershipId = request.getDealershipId();

        Technician technician = findFirstFreeTechnician(
                technicianRepository.findByStatusAndDealershipIdOrderByIdAsc(
                        TechnicianStatus.AVAILABLE, dealershipId),
                serviceType.getRequiredSkills(),
                request.getAppointmentDate(),
                request.getStartTime(),
                endTime,
                null
        );
        if (technician == null) {
            return AvailabilityResponseDto.builder()
                    .available(false)
                    .duration(serviceType.getDurationMinutes())
                    .endTime(endTime)
                    .build();
        }

        ServiceBay serviceBay = findFirstFreeServiceBay(
                serviceBayRepository.findByStatusAndDealershipIdOrderByIdAsc(
                        ServiceBayStatus.AVAILABLE, dealershipId),
                request.getAppointmentDate(),
                request.getStartTime(),
                endTime,
                null
        );
        if (serviceBay == null) {
            return AvailabilityResponseDto.builder()
                    .available(false)
                    .technicianName(technician.getName())
                    .duration(serviceType.getDurationMinutes())
                    .endTime(endTime)
                    .build();
        }

        return AvailabilityResponseDto.builder()
                .available(true)
                .technicianName(technician.getName())
                .serviceBayName(serviceBay.getName())
                .duration(serviceType.getDurationMinutes())
                .endTime(endTime)
                .build();
    }

    @Override
    @Transactional
    public AppointmentResponseDto createAppointment(CreateAppointmentRequestDto request) {
        log.info("Creating appointment for customerId={} on {} {}",
                request.getCustomerId(), request.getAppointmentDate(), request.getStartTime());

        validateNotPast(request.getAppointmentDate(), request.getStartTime());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new DataNotfoundException(ERR_CUSTOMER_NOT_FOUND));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new DataNotfoundException(ERR_VEHICLE_NOT_FOUND));
        if (!vehicle.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException(ERR_VEHICLE_NOT_OWNED_BY_CUSTOMER);
        }

        ServiceType serviceType = serviceTypeRepository.findWithSkillsById(request.getServiceTypeId())
                .orElseThrow(() -> new DataNotfoundException(ERR_SERVICE_TYPE_NOT_FOUND));
        Dealership dealership = dealershipRepository.findById(request.getDealershipId())
                .orElseThrow(() -> new DataNotfoundException(ERR_DEALERSHIP_NOT_FOUND));

        LocalTime endTime = request.getStartTime().plusMinutes(serviceType.getDurationMinutes());
        Long dealershipId = dealership.getId();

        List<Technician> technicians = technicianRepository.findAvailableForUpdate(dealershipId);
        // Initialize skills inside lock transaction
        technicians.forEach(t -> t.getSkills().size());

        Technician technician = findFirstFreeTechnician(
                technicians, serviceType.getRequiredSkills(),
                request.getAppointmentDate(), request.getStartTime(), endTime, null);
        if (technician == null) {
            throw new DataConflictException(ERR_NO_AVAILABLE_TECHNICIAN);
        }

        List<ServiceBay> serviceBays = serviceBayRepository.findAvailableForUpdate(dealershipId);
        ServiceBay serviceBay = findFirstFreeServiceBay(
                serviceBays, request.getAppointmentDate(), request.getStartTime(), endTime, null);
        if (serviceBay == null) {
            throw new DataConflictException(ERR_NO_AVAILABLE_SERVICE_BAY);
        }

        Appointment appointment = Appointment.builder()
                .customer(customer)
                .vehicle(vehicle)
                .technician(technician)
                .serviceBay(serviceBay)
                .dealership(dealership)
                .serviceType(serviceType)
                .appointmentDate(request.getAppointmentDate())
                .startTime(request.getStartTime())
                .endTime(endTime)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        return appointmentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public AppointmentResponseDto cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findWithDetailsById(id)
                .orElseThrow(() -> new DataNotfoundException(ERR_APPOINTMENT_NOT_FOUND));
        if (appointment.getStatus() != AppointmentStatus.PENDING
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException(ERR_APPOINTMENT_INVALID_STATUS);
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponseDto updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findWithDetailsById(id)
                .orElseThrow(() -> new DataNotfoundException(ERR_APPOINTMENT_NOT_FOUND));
        validateStatusTransition(appointment.getStatus(), status);
        appointment.setStatus(status);
        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional
    public AppointmentResponseDto rescheduleAppointment(Long id, RescheduleAppointmentRequestDto request) {
        Appointment appointment = appointmentRepository.findWithDetailsById(id)
                .orElseThrow(() -> new DataNotfoundException(ERR_APPOINTMENT_NOT_FOUND));

        if (appointment.getStatus() != AppointmentStatus.PENDING
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BadRequestException(ERR_APPOINTMENT_INVALID_STATUS);
        }

        validateNotPast(request.getAppointmentDate(), request.getStartTime());

        ServiceType serviceType = serviceTypeRepository.findWithSkillsById(appointment.getServiceType().getId())
                .orElseThrow(() -> new DataNotfoundException(ERR_SERVICE_TYPE_NOT_FOUND));
        LocalTime endTime = request.getStartTime().plusMinutes(serviceType.getDurationMinutes());
        Long dealershipId = appointment.getDealership().getId();

        List<Technician> technicians = technicianRepository.findAvailableForUpdate(dealershipId);
        technicians.forEach(t -> t.getSkills().size());
        Technician technician = findFirstFreeTechnician(
                technicians, serviceType.getRequiredSkills(),
                request.getAppointmentDate(), request.getStartTime(), endTime, id);
        if (technician == null) {
            throw new DataConflictException(ERR_NO_AVAILABLE_TECHNICIAN);
        }

        List<ServiceBay> serviceBays = serviceBayRepository.findAvailableForUpdate(dealershipId);
        ServiceBay serviceBay = findFirstFreeServiceBay(
                serviceBays, request.getAppointmentDate(), request.getStartTime(), endTime, id);
        if (serviceBay == null) {
            throw new DataConflictException(ERR_NO_AVAILABLE_SERVICE_BAY);
        }

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(endTime);
        appointment.setTechnician(technician);
        appointment.setServiceBay(serviceBay);

        return appointmentMapper.toResponseDto(appointmentRepository.save(appointment));
    }

    private void validateStatusTransition(AppointmentStatus current, AppointmentStatus next) {
        if (current == next) {
            return;
        }
        Set<AppointmentStatus> allowed = switch (current) {
            case PENDING -> EnumSet.of(AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED);
            case CONFIRMED -> EnumSet.of(AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED);
            case COMPLETED, CANCELLED -> EnumSet.noneOf(AppointmentStatus.class);
        };
        if (!allowed.contains(next)) {
            throw new BadRequestException(ERR_APPOINTMENT_INVALID_STATUS);
        }
    }

    private void validateNotPast(LocalDate appointmentDate, LocalTime startTime) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, startTime);
        LocalDateTime now = LocalDateTime.now(ZONE_ID);
        if (!appointmentDateTime.isAfter(now)) {
            throw new BadRequestException(ERR_APPOINTMENT_IN_PAST);
        }
    }

    private Technician findFirstFreeTechnician(
            List<Technician> technicians,
            Set<Skill> requiredSkills,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            Long excludeAppointmentId) {
        for (Technician technician : technicians) {
            if (!hasAllRequiredSkills(technician, requiredSkills)) {
                continue;
            }
            if (!hasOverlap(technician.getId(), null, appointmentDate, startTime, endTime, excludeAppointmentId)) {
                return technician;
            }
        }
        return null;
    }

    private boolean hasAllRequiredSkills(Technician technician, Set<Skill> requiredSkills) {
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            return true;
        }
        Set<Long> technicianSkillIds = technician.getSkills().stream()
                .map(Skill::getId)
                .collect(Collectors.toSet());
        return requiredSkills.stream().allMatch(skill -> technicianSkillIds.contains(skill.getId()));
    }

    private ServiceBay findFirstFreeServiceBay(
            List<ServiceBay> serviceBays,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            Long excludeAppointmentId) {
        for (ServiceBay serviceBay : serviceBays) {
            if (!hasOverlap(null, serviceBay.getId(), appointmentDate, startTime, endTime, excludeAppointmentId)) {
                return serviceBay;
            }
        }
        return null;
    }

    /**
     * Overlap: existing.start &lt; newEnd AND existing.end &gt; newStart.
     */
    private boolean hasOverlap(
            Long technicianId,
            Long serviceBayId,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            Long excludeAppointmentId) {
        if (technicianId != null) {
            if (excludeAppointmentId != null) {
                return appointmentRepository
                        .existsByTechnicianIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
                                technicianId, appointmentDate, BLOCKING_STATUSES, endTime, startTime, excludeAppointmentId);
            }
            return appointmentRepository
                    .existsByTechnicianIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                            technicianId, appointmentDate, BLOCKING_STATUSES, endTime, startTime);
        }
        if (excludeAppointmentId != null) {
            return appointmentRepository
                    .existsByServiceBayIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThanAndIdNot(
                            serviceBayId, appointmentDate, BLOCKING_STATUSES, endTime, startTime, excludeAppointmentId);
        }
        return appointmentRepository
                .existsByServiceBayIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        serviceBayId, appointmentDate, BLOCKING_STATUSES, endTime, startTime);
    }
}
