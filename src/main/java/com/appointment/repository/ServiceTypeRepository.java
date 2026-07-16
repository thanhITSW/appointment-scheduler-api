package com.appointment.repository;

import com.appointment.entity.ServiceType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceTypeRepository extends JpaRepository<ServiceType, Long> {

    @EntityGraph(attributePaths = "requiredSkills")
    List<ServiceType> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = "requiredSkills")
    @Query("SELECT st FROM ServiceType st WHERE st.id = :id")
    Optional<ServiceType> findWithSkillsById(@Param("id") Long id);
}
