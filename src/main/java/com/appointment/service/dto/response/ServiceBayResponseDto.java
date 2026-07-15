package com.appointment.service.dto.response;

import com.appointment.enumeration.ServiceBayStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBayResponseDto {

    private Long id;
    private String name;
    private ServiceBayStatus status;
}
