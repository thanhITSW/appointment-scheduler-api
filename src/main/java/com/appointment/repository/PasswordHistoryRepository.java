package com.appointment.repository;

import com.appointment.entity.PasswordHistory;
import com.appointment.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for password history management
 */
@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {
    
    /**
     * Find password history for a user, ordered by creation date (newest first)
     * @param user the user
     * @return list of password history entries
     */
    List<PasswordHistory> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find recent password history for a user (last N entries)
     * @param userId the id of user
     * @return list of recent password history entries
     */
    @Query("SELECT ph FROM PasswordHistory ph WHERE ph.user.id = :userId ORDER BY ph.createdAt DESC")
    List<PasswordHistory> findRecentPasswordHistory(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Find password history for a user within a specific time period
     * @param user the user
     * @param since start date
     * @return list of password history entries since the specified date
     */
    List<PasswordHistory> findByUserAndCreatedAtAfterOrderByCreatedAtDesc(User user, Instant since);
    
    /**
     * Check if a password hash exists in user's history
     * @param user the user
     * @param passwordHash the password hash to check
     * @return true if password hash exists in history
     */
    boolean existsByUserAndPasswordHash(User user, String passwordHash);
    
    /**
     * Count active password history entries for a user
     * @param user the user
     * @return count of active password history entries
     */
    long countByUserAndActiveTrue(User user);
    
    /**
     * Delete old password history entries for a user (keep only recent ones)
     * @param user the user
     * @param keepCount number of recent entries to keep
     */
    @Modifying
    @Query(value = "DELETE ph FROM password_history ph " +
           "WHERE ph.fk_user_id = :userId " +
           "AND ph.id NOT IN (" +
           "  SELECT * FROM (" +
           "    SELECT ph2.id FROM password_history ph2 " +
           "    WHERE ph2.fk_user_id = :userId " +
           "    ORDER BY ph2.created_at DESC " +
           "    LIMIT :keepCount" +
           "  ) AS recent_entries" +
           ")", nativeQuery = true)
    void deleteOldPasswordHistory(@Param("userId") Long userId, @Param("keepCount") int keepCount);
    
    /**
     * Find password history entries older than specified date
     * @param user the user
     * @param beforeDate entries before this date
     * @return list of old password history entries
     */
    List<PasswordHistory> findByUserAndCreatedAtBefore(User user, Instant beforeDate);
}
