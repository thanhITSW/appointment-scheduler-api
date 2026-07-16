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

@Table(name = "login_session")
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginSession extends AbstractAuditingEntity  implements Serializable {

    private static final long serialVersionUID = -1295680884346747222L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_address", columnDefinition = "varchar(255)")
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "varchar(255)")
    private String userAgent;

    @Column(name = "location", columnDefinition = "varchar(255)")
    private String location;

    @Column(name = "active", columnDefinition = "tinyint(1) default 1")
    private boolean active;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "session_id", columnDefinition = "varchar(255)")
    private String sessionId;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    @ToStringExclude
    private User user;

    // Helper methods for session validation
    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    public boolean isValid() {
        return active && !isExpired();
    }

    public boolean canRefresh() {
        return isValid();
    }

    public long getTtlSeconds() {
        if (expiresAt == null) return 0;
        return Math.max(0, expiresAt.getEpochSecond() - Instant.now().getEpochSecond());
    }

    public boolean isExpiringSoon(long minutesThreshold) {
        if (expiresAt == null) return false;
        Instant threshold = Instant.now().plusSeconds(minutesThreshold * 60);
        return expiresAt.isBefore(threshold);
    }

    public void extendExpiration(Instant newExpiration) {
        if (newExpiration.isAfter(Instant.now())) {
            this.expiresAt = newExpiration;
        }
    }

    public void deactivate() {
        this.active = false;
    }
}
