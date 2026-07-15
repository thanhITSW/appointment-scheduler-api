package com.appointment.repository;

import com.appointment.entity.Technician;
import com.appointment.enumeration.TechnicianStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    @EntityGraph(attributePaths = {"skills", "dealership"})
    List<Technician> findByStatusAndDealershipIdOrderByIdAsc(TechnicianStatus status, Long dealershipId);

    @EntityGraph(attributePaths = {"skills", "dealership"})
    List<Technician> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = {"skills", "dealership"})
    @Query("SELECT t FROM Technician t WHERE t.id = :id")
    Optional<Technician> findWithSkillsById(@Param("id") Long id);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCodeAndIdNot(String employeeCode, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT t FROM Technician t
            WHERE t.status = com.appointment.enumeration.TechnicianStatus.AVAILABLE
              AND t.dealership.id = :dealershipId
            ORDER BY t.id ASC
            """)
    List<Technician> findAvailableForUpdate(@Param("dealershipId") Long dealershipId);
}
