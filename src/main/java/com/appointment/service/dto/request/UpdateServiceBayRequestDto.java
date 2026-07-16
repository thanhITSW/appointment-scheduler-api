package com.appointment.service.dto.request;

import com.appointment.enumeration.ServiceBayStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateServiceBayRequestDto {

    @NotBlank
    private String name;

    @NotNull
    private ServiceBayStatus status;

    @NotNull
    private Long dealershipId;
}
