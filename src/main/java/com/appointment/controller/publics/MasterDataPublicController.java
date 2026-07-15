package com.appointment.controller.publics;

import com.appointment.service.MasterDataService;
import com.appointment.service.dto.response.DealershipResponseDto;
import com.appointment.service.dto.response.ServiceTypeResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Master Data", description = "Public APIs for dealerships and service types")
public class MasterDataPublicController {

    private final MasterDataService masterDataService;

    @GetMapping("/api/v1/public/service-types")
    @Operation(summary = "List service types", description = "Get all available service types")
    public ResponseEntity<List<ServiceTypeResponseDto>> getServiceTypes() {
        return ResponseEntity.ok(masterDataService.getServiceTypes());
    }

    @GetMapping("/api/v1/public/dealerships")
    @Operation(summary = "List dealerships", description = "Get all dealerships")
    public ResponseEntity<List<DealershipResponseDto>> getDealerships() {
        return ResponseEntity.ok(masterDataService.getDealerships());
    }
}
