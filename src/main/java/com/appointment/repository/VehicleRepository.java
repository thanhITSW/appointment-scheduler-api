package com.appointment.repository;

import com.appointment.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByCustomerIdOrderByIdAsc(Long customerId);

    Optional<Vehicle> findByIdAndCustomerId(Long id, Long customerId);
}
