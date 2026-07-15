package com.appointment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.appointment.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    
    Optional<User> findByEmailAndDeletedFalse(String email);
    Optional<User> findByIdAndDeletedFalse(Long id);
    Optional<User> findByEmployeeIdAndDeletedFalse(String employeeId);

    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndDeletedFalse(String email);
    boolean existsByEmployeeIdIgnoreCaseAndDeletedFalse(String employeeId);


    
    // Methods for DeleteHistory filtering by fullName
    List<User> findByFullNameContainingIgnoreCase(String fullName);
    List<User> findByFullNameIgnoreCase(String fullName);
    List<User> findByFullNameInIgnoreCase(List<String> fullNames);
}
