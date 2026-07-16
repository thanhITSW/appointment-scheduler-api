package com.appointment.repository;

import com.appointment.entity.LoginSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoginSessionRepository extends JpaRepository<LoginSession, Long> {

    @Query("SELECT session "
           + "FROM LoginSession session "
           + "WHERE session.active = true AND session.sessionId = :sessionId "
           + "AND session.active = true AND session.user.id= :userId")
    Optional<LoginSession> findByActiveSession(String sessionId, Long userId);

    @Query("SELECT session "
           + "FROM LoginSession session "
           + "WHERE session.active = true AND session.sessionId = :sessionId")
    Optional<LoginSession> findByActiveSession(String sessionId);

    @Query("UPDATE LoginSession session "
           + "SET session.active= false "
           + "WHERE session.sessionId= :sessionId AND session.user.id = :userId")
    @Modifying
    void inactiveSession(Long userId, String sessionId);

    List<LoginSession> findByUserIdAndActiveIsTrueAndExpiresAtGreaterThan(Long userId, Instant now);


}
