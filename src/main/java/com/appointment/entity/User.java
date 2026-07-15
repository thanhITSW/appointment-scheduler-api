package com.appointment.entity;

import com.appointment.enumeration.UserRole;
import com.appointment.enumeration.UserStatus;
import com.appointment.jpa.AbstractAuditingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Formula;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractAuditingEntity  implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status;

    @Column(name = "note")
    private String note;

    @Formula("IF(is_deleted=1, NULL, 1)")
    private Boolean isNotArchived;

    @Column(name = "is_deleted", columnDefinition = "tinyint(1) default 0")
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "is_blocked", columnDefinition = "tinyint(1) default 0")
    @Builder.Default
    private boolean blocked = false;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @Column(name = "password_expires_at")
    private Instant passwordExpiresAt;

    @Column(name = "failed_login_attempts", columnDefinition = "int default 0")
    @Builder.Default
    private int failedLoginAttempts = 0;

    @Column(name = "last_failed_login_at")
    private Instant lastFailedLoginAt;

    @Column(name = "account_locked_until")
    private Instant accountLockedUntil;

    @Column(name = "two_fa_enabled", columnDefinition = "tinyint(1) default 0")
    @Builder.Default
    private boolean twoFaEnabled = false;

    @Column(name = "phone_number", columnDefinition = "TEXT")
    private String phoneNumber;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "date_of_birth", columnDefinition = "TEXT")
    private String dateOfBirth;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    private List<LoginSession> loginSessions = new ArrayList<>();

    public boolean isUserBlocked() {
        return blocked;
    }

    public boolean isUserEnable() {
        return !blocked;
    }

    public void addLoginSession(LoginSession session) {
        loginSessions.add(session);
        session.setUser(this);
    }
    
    /**
     * Check if the user account is currently locked
     * @return true if account is locked
     */
    public boolean isAccountLocked() {
        return accountLockedUntil != null && accountLockedUntil.isAfter(Instant.now());
    }
    
    /**
     * Check if password has expired
     * @return true if password has expired
     */
    public boolean isPasswordExpired() {
        return passwordExpiresAt != null && passwordExpiresAt.isBefore(Instant.now());
    }

    /**
     * Increment failed login attempts
     */
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        this.lastFailedLoginAt = Instant.now();
    }
    
    /**
     * Reset failed login attempts
     */
    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lastFailedLoginAt = null;
        this.accountLockedUntil = null;
    }
    
    /**
     * Lock account for specified duration
     * @param lockoutMinutes duration in minutes
     */
    public void lockAccount(int lockoutMinutes) {
        this.accountLockedUntil = Instant.now().plusSeconds(lockoutMinutes * 60l);
    }
}
