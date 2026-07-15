package com.appointment.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityResponseDto {

    private boolean available;
    private String technicianName;
    private String serviceBayName;
    private Integer duration;
    private LocalTime endTime;
}
