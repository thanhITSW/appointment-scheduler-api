package com.appointment.controller.publics;

import com.appointment.enumeration.AppointmentStatus;
import com.appointment.service.AppointmentQueryService;
import com.appointment.service.AppointmentService;
import com.appointment.service.dto.request.CheckAvailabilityRequestDto;
import com.appointment.service.dto.request.CreateAppointmentRequestDto;
import com.appointment.service.dto.response.AppointmentResponseDto;
import com.appointment.service.dto.response.AvailabilityResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@DisplayName("AppointmentPublicController Tests")
class AppointmentPublicControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @Mock
    private AppointmentQueryService appointmentQueryService;

    @InjectMocks
    private AppointmentPublicController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private LocalDate futureDate;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        futureDate = LocalDate.now().plusDays(5);
    }

    @Test
    @DisplayName("POST /check-availability returns availability payload")
    void checkAvailability_ok() throws Exception {
        CheckAvailabilityRequestDto request = CheckAvailabilityRequestDto.builder()
                .dealershipId(1L)
                .serviceTypeId(1L)
                .appointmentDate(futureDate)
                .startTime(LocalTime.of(10, 0))
                .build();

        when(appointmentService.checkAvailability(any(CheckAvailabilityRequestDto.class)))
                .thenReturn(AvailabilityResponseDto.builder()
                        .available(true)
                        .technicianName("Alex Rivera")
                        .serviceBayName("Bay 1")
                        .duration(60)
                        .endTime(LocalTime.of(11, 0))
                        .build());

        mockMvc.perform(post("/api/v1/public/appointments/check-availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.technicianName").value("Alex Rivera"))
                .andExpect(jsonPath("$.serviceBayName").value("Bay 1"));

        verify(appointmentService).checkAvailability(any(CheckAvailabilityRequestDto.class));
    }

    @Test
    @DisplayName("POST /appointments creates appointment")
    void createAppointment_created() throws Exception {
        CreateAppointmentRequestDto request = CreateAppointmentRequestDto.builder()
                .customerId(1L)
                .vehicleId(1L)
                .serviceTypeId(1L)
                .dealershipId(1L)
                .appointmentDate(futureDate)
                .startTime(LocalTime.of(10, 0))
                .build();

        when(appointmentService.createAppointment(any(CreateAppointmentRequestDto.class)))
                .thenReturn(AppointmentResponseDto.builder()
                        .id(99L)
                        .customerId(1L)
                        .status(AppointmentStatus.CONFIRMED)
                        .appointmentDate(futureDate)
                        .startTime(LocalTime.of(10, 0))
                        .endTime(LocalTime.of(11, 0))
                        .build());

        mockMvc.perform(post("/api/v1/public/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(99))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));

        verify(appointmentService).createAppointment(any(CreateAppointmentRequestDto.class));
    }

    @Test
    @DisplayName("GET /appointments/{id} returns appointment")
    void getAppointment_ok() throws Exception {
        when(appointmentQueryService.getById(5L))
                .thenReturn(AppointmentResponseDto.builder()
                        .id(5L)
                        .status(AppointmentStatus.CONFIRMED)
                        .build());

        mockMvc.perform(get("/api/v1/public/appointments/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /appointments searches with pagination")
    void searchAppointments_ok() throws Exception {
        when(appointmentQueryService.search(isNull(), eq(1L), isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(
                        List.of(AppointmentResponseDto.builder().id(1L).status(AppointmentStatus.PENDING).build()),
                        PageRequest.of(0, 20),
                        1));

        mockMvc.perform(get("/api/v1/public/appointments")
                        .param("customerId", "1")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
