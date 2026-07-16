package com.appointment.controller.privates;

import com.appointment.service.CustomerService;
import com.appointment.service.dto.request.UpdateCustomerRequestDto;
import com.appointment.service.dto.response.CustomerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/private/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Private Customers", description = "Staff APIs for customer management")
@SecurityRequirement(name = "bearerAuth")
public class CustomerPrivateController {

    private final CustomerService customerService;

    @GetMapping("/{id}")
    @Operation(summary = "Get customer")
    public ResponseEntity<CustomerResponseDto> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<CustomerResponseDto> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerRequestDto request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }
}
