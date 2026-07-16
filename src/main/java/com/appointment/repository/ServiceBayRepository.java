package com.appointment.repository;

import com.appointment.entity.ServiceBay;
import com.appointment.enumeration.ServiceBayStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceBayRepository extends JpaRepository<ServiceBay, Long> {

    List<ServiceBay> findByStatusAndDealershipIdOrderByIdAsc(ServiceBayStatus status, Long dealershipId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT sb FROM ServiceBay sb
            WHERE sb.status = com.appointment.enumeration.ServiceBayStatus.AVAILABLE
              AND sb.dealership.id = :dealershipId
            ORDER BY sb.id ASC
            """)
    List<ServiceBay> findAvailableForUpdate(@Param("dealershipId") Long dealershipId);
}
