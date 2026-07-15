package com.appointment.repository;

import com.appointment.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    Optional<Skill> findByCode(String code);

    boolean existsByCode(String code);

    List<Skill> findByIdIn(Collection<Long> ids);
}
