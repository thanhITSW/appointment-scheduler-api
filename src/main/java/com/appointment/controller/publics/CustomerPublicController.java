package com.appointment.controller.publics;

import com.appointment.service.CustomerService;
import com.appointment.service.VehicleService;
import com.appointment.service.dto.request.CreateCustomerRequestDto;
import com.appointment.service.dto.request.CreateVehicleRequestDto;
import com.appointment.service.dto.response.CustomerResponseDto;
import com.appointment.service.dto.response.VehicleResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customers", description = "Public APIs for customer and vehicle management")
public class CustomerPublicController {

    private final CustomerService customerService;
    private final VehicleService vehicleService;

    @GetMapping
    @Operation(summary = "Search customers", description = "Search customers by keyword (name, phone, email)")
    public ResponseEntity<List<CustomerResponseDto>> searchCustomers(
            @Parameter(description = "Optional search keyword")
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(customerService.searchCustomers(keyword));
    }

    @PostMapping
    @Operation(summary = "Create customer", description = "Create a new customer")
    public ResponseEntity<CustomerResponseDto> createCustomer(
            @Valid @RequestBody CreateCustomerRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }

    @GetMapping("/{customerId}/vehicles")
    @Operation(summary = "List customer vehicles", description = "Get vehicles owned by a customer")
    public ResponseEntity<List<VehicleResponseDto>> getCustomerVehicles(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(vehicleService.getCustomerVehicles(customerId));
    }

    @PostMapping("/{customerId}/vehicles")
    @Operation(summary = "Create vehicle", description = "Create a vehicle for a customer")
    public ResponseEntity<VehicleResponseDto> createVehicle(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateVehicleRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.createVehicle(customerId, request));
    }
}
