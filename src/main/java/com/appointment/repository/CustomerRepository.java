package com.appointment.repository;

import com.appointment.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("""
            SELECT c FROM Customer c
            WHERE LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Customer> searchByKeyword(@Param("keyword") String keyword);
}
