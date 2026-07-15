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
import com.appointment.service.dto.request.CheckAvailabilityRequestDto;
import com.appointment.service.dto.request.CreateAppointmentRequestDto;
import com.appointment.service.dto.response.AppointmentResponseDto;
import com.appointment.service.dto.response.AvailabilityResponseDto;
import com.appointment.service.mapper.AppointmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.appointment.constant.ErrorCodeConstant.ERR_APPOINTMENT_IN_PAST;
import static com.appointment.constant.ErrorCodeConstant.ERR_APPOINTMENT_INVALID_STATUS;
import static com.appointment.constant.ErrorCodeConstant.ERR_CUSTOMER_NOT_FOUND;
import static com.appointment.constant.ErrorCodeConstant.ERR_NO_AVAILABLE_TECHNICIAN;
import static com.appointment.constant.ErrorCodeConstant.ERR_VEHICLE_NOT_OWNED_BY_CUSTOMER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentServiceImpl Tests")
class AppointmentServiceImplTest {

    private static final Set<AppointmentStatus> BLOCKING_STATUSES = Set.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.CONFIRMED
    );

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private ServiceTypeRepository serviceTypeRepository;
    @Mock
    private DealershipRepository dealershipRepository;
    @Mock
    private TechnicianRepository technicianRepository;
    @Mock
    private ServiceBayRepository serviceBayRepository;
    @Mock
    private AppointmentMapper appointmentMapper;

    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private LocalDate futureDate;
    private LocalTime startTime;
    private ServiceType serviceType;
    private Dealership dealership;
    private Technician technician;
    private ServiceBay serviceBay;
    private Customer customer;
    private Vehicle vehicle;
    private Skill oilSkill;

    @BeforeEach
    void setUp() {
        futureDate = LocalDate.now().plusDays(3);
        startTime = LocalTime.of(10, 0);

        oilSkill = Skill.builder()
                .id(1L)
                .code("OIL")
                .name("Oil Change")
                .build();

        serviceType = ServiceType.builder()
                .id(1L)
                .name("Oil Change")
                .durationMinutes(30)
                .requiredSkills(new HashSet<>())
                .build();

        dealership = Dealership.builder()
                .id(1L)
                .name("Downtown Service Center")
                .address("100 Main Street")
                .build();

        technician = Technician.builder()
                .id(1L)
                .name("Alex Rivera")
                .employeeCode("TECH-001")
                .status(TechnicianStatus.AVAILABLE)
                .skills(new HashSet<>())
                .build();

        serviceBay = ServiceBay.builder()
                .id(1L)
                .name("Bay 1")
                .status(ServiceBayStatus.AVAILABLE)
                .build();

        customer = Customer.builder()
                .id(10L)
                .firstName("Jane")
                .lastName("Doe")
                .phone("555-0100")
                .email("jane@example.com")
                .build();

        vehicle = Vehicle.builder()
                .id(20L)
                .customer(customer)
                .vin("VIN123")
                .licensePlate("ABC-123")
                .make("Toyota")
                .model("Camry")
                .year(2022)
                .build();
    }

    @Test
    @DisplayName("checkAvailability returns available when tech and bay are free")
    void checkAvailability_available() {
        CheckAvailabilityRequestDto request = CheckAvailabilityRequestDto.builder()
                .dealershipId(1L)
                .serviceTypeId(1L)
                .appointmentDate(futureDate)
                .startTime(startTime)
                .build();

        when(serviceTypeRepository.findWithSkillsById(1L)).thenReturn(Optional.of(serviceType));
        when(dealershipRepository.findById(1L)).thenReturn(Optional.of(dealership));
        when(technicianRepository.findByStatusOrderByIdAsc(TechnicianStatus.AVAILABLE))
                .thenReturn(List.of(technician));
        when(serviceBayRepository.findByStatusOrderByIdAsc(ServiceBayStatus.AVAILABLE))
                .thenReturn(List.of(serviceBay));
        when(appointmentRepository
                .existsByTechnicianIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        eq(1L), eq(futureDate), eq(BLOCKING_STATUSES), eq(LocalTime.of(10, 30)), eq(startTime)))
                .thenReturn(false);
        when(appointmentRepository
                .existsByServiceBayIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        eq(1L), eq(futureDate), eq(BLOCKING_STATUSES), eq(LocalTime.of(10, 30)), eq(startTime)))
                .thenReturn(false);

        AvailabilityResponseDto result = appointmentService.checkAvailability(request);

        assertThat(result.isAvailable()).isTrue();
        assertThat(result.getTechnicianName()).isEqualTo("Alex Rivera");
        assertThat(result.getServiceBayName()).isEqualTo("Bay 1");
        assertThat(result.getDuration()).isEqualTo(30);
        assertThat(result.getEndTime()).isEqualTo(LocalTime.of(10, 30));
    }

    @Test
    @DisplayName("checkAvailability returns unavailable when no technician is free")
    void checkAvailability_noTechnician() {
        CheckAvailabilityRequestDto request = CheckAvailabilityRequestDto.builder()
                .dealershipId(1L)
                .serviceTypeId(1L)
                .appointmentDate(futureDate)
                .startTime(startTime)
                .build();

        when(serviceTypeRepository.findWithSkillsById(1L)).thenReturn(Optional.of(serviceType));
        when(dealershipRepository.findById(1L)).thenReturn(Optional.of(dealership));
        when(technicianRepository.findByStatusOrderByIdAsc(TechnicianStatus.AVAILABLE))
                .thenReturn(Collections.emptyList());

        AvailabilityResponseDto result = appointmentService.checkAvailability(request);

        assertThat(result.isAvailable()).isFalse();
        assertThat(result.getTechnicianName()).isNull();
        assertThat(result.getServiceBayName()).isNull();
        assertThat(result.getDuration()).isEqualTo(30);
        assertThat(result.getEndTime()).isEqualTo(LocalTime.of(10, 30));
    }

    @Test
    @DisplayName("createAppointment success locks available technicians and bays")
    void createAppointment_success() {
        CreateAppointmentRequestDto request = CreateAppointmentRequestDto.builder()
                .customerId(10L)
                .vehicleId(20L)
                .serviceTypeId(1L)
                .dealershipId(1L)
                .appointmentDate(futureDate)
                .startTime(startTime)
                .build();

        Appointment saved = Appointment.builder()
                .id(100L)
                .customer(customer)
                .vehicle(vehicle)
                .technician(technician)
                .serviceBay(serviceBay)
                .dealership(dealership)
                .serviceType(serviceType)
                .appointmentDate(futureDate)
                .startTime(startTime)
                .endTime(LocalTime.of(10, 30))
                .status(AppointmentStatus.PENDING)
                .build();

        AppointmentResponseDto responseDto = AppointmentResponseDto.builder()
                .id(100L)
                .status(AppointmentStatus.PENDING)
                .build();

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(20L)).thenReturn(Optional.of(vehicle));
        when(serviceTypeRepository.findWithSkillsById(1L)).thenReturn(Optional.of(serviceType));
        when(dealershipRepository.findById(1L)).thenReturn(Optional.of(dealership));
        when(technicianRepository.findAvailableForUpdate()).thenReturn(List.of(technician));
        when(serviceBayRepository.findAvailableForUpdate()).thenReturn(List.of(serviceBay));
        when(appointmentRepository
                .existsByTechnicianIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        eq(1L), eq(futureDate), eq(BLOCKING_STATUSES), eq(LocalTime.of(10, 30)), eq(startTime)))
                .thenReturn(false);
        when(appointmentRepository
                .existsByServiceBayIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        eq(1L), eq(futureDate), eq(BLOCKING_STATUSES), eq(LocalTime.of(10, 30)), eq(startTime)))
                .thenReturn(false);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(saved);
        when(appointmentMapper.toResponseDto(saved)).thenReturn(responseDto);

        AppointmentResponseDto result = appointmentService.createAppointment(request);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.PENDING);
        verify(technicianRepository).findAvailableForUpdate();
        verify(serviceBayRepository).findAvailableForUpdate();
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    @DisplayName("createAppointment rejects past appointment times")
    void createAppointment_pastRejects() {
        CreateAppointmentRequestDto request = CreateAppointmentRequestDto.builder()
                .customerId(10L)
                .vehicleId(20L)
                .serviceTypeId(1L)
                .dealershipId(1L)
                .appointmentDate(LocalDate.now().minusDays(1))
                .startTime(startTime)
                .build();

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ERR_APPOINTMENT_IN_PAST);
    }

    @Test
    @DisplayName("createAppointment throws when customer is missing")
    void createAppointment_customerNotFound() {
        CreateAppointmentRequestDto request = CreateAppointmentRequestDto.builder()
                .customerId(99L)
                .vehicleId(20L)
                .serviceTypeId(1L)
                .dealershipId(1L)
                .appointmentDate(futureDate)
                .startTime(startTime)
                .build();

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
                .isInstanceOf(DataNotfoundException.class)
                .hasMessage(ERR_CUSTOMER_NOT_FOUND);
    }

    @Test
    @DisplayName("createAppointment throws when vehicle is not owned by customer")
    void createAppointment_vehicleNotOwnedByCustomer() {
        Customer otherCustomer = Customer.builder()
                .id(11L)
                .firstName("Other")
                .lastName("Owner")
                .phone("555-9999")
                .build();
        Vehicle otherVehicle = Vehicle.builder()
                .id(20L)
                .customer(otherCustomer)
                .vin("VIN999")
                .licensePlate("ZZZ-999")
                .make("Honda")
                .model("Civic")
                .year(2020)
                .build();

        CreateAppointmentRequestDto request = CreateAppointmentRequestDto.builder()
                .customerId(10L)
                .vehicleId(20L)
                .serviceTypeId(1L)
                .dealershipId(1L)
                .appointmentDate(futureDate)
                .startTime(startTime)
                .build();

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(20L)).thenReturn(Optional.of(otherVehicle));

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ERR_VEHICLE_NOT_OWNED_BY_CUSTOMER);
    }

    @Test
    @DisplayName("cancelAppointment cancels PENDING appointment")
    void cancelAppointment_success() {
        Appointment appointment = Appointment.builder()
                .id(100L)
                .customer(customer)
                .vehicle(vehicle)
                .technician(technician)
                .serviceBay(serviceBay)
                .dealership(dealership)
                .serviceType(serviceType)
                .appointmentDate(futureDate)
                .startTime(startTime)
                .endTime(LocalTime.of(10, 30))
                .status(AppointmentStatus.PENDING)
                .build();

        AppointmentResponseDto responseDto = AppointmentResponseDto.builder()
                .id(100L)
                .status(AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepository.findWithDetailsById(100L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toResponseDto(appointment)).thenReturn(responseDto);

        AppointmentResponseDto result = appointmentService.cancelAppointment(100L);

        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    @DisplayName("cancelAppointment rejects COMPLETED appointment")
    void cancelAppointment_invalidStatus() {
        Appointment appointment = Appointment.builder()
                .id(100L)
                .customer(customer)
                .vehicle(vehicle)
                .technician(technician)
                .serviceBay(serviceBay)
                .dealership(dealership)
                .serviceType(serviceType)
                .appointmentDate(futureDate)
                .startTime(startTime)
                .endTime(LocalTime.of(10, 30))
                .status(AppointmentStatus.COMPLETED)
                .build();

        when(appointmentRepository.findWithDetailsById(100L)).thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.cancelAppointment(100L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ERR_APPOINTMENT_INVALID_STATUS);
    }

    @Test
    @DisplayName("createAppointment rejects when technician lacks required skills")
    void createAppointment_skillMismatch() {
        serviceType.setRequiredSkills(Set.of(oilSkill));
        technician.setSkills(new HashSet<>());

        CreateAppointmentRequestDto request = CreateAppointmentRequestDto.builder()
                .customerId(10L)
                .vehicleId(20L)
                .serviceTypeId(1L)
                .dealershipId(1L)
                .appointmentDate(futureDate)
                .startTime(startTime)
                .build();

        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(20L)).thenReturn(Optional.of(vehicle));
        when(serviceTypeRepository.findWithSkillsById(1L)).thenReturn(Optional.of(serviceType));
        when(dealershipRepository.findById(1L)).thenReturn(Optional.of(dealership));
        when(technicianRepository.findAvailableForUpdate()).thenReturn(List.of(technician));

        assertThatThrownBy(() -> appointmentService.createAppointment(request))
                .isInstanceOf(DataConflictException.class)
                .hasMessage(ERR_NO_AVAILABLE_TECHNICIAN);
    }
}
