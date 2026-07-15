package com.appointment.entity;

import com.appointment.jpa.AbstractAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.io.Serializable;
import java.time.Instant;

/**
 * Entity to track password history for users
 * Used to prevent password reuse based on policy requirements
 */
@Entity
@Table(name = "password_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PasswordHistory extends AbstractAuditingEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "fk_user_id", nullable = false)
    @ToStringExclude
    private User user;
    
    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;
    
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    
    @Column(name = "is_active", columnDefinition = "tinyint(1) default 1")
    @Builder.Default
    private boolean active = true;

}
