package com.appointment.security.handler;

import com.appointment.config.ApplicationProperties;
import com.appointment.entity.User;
import com.appointment.exception.BadRequestException;
import com.appointment.repository.UserRepository;
import com.appointment.security.model.CustomUserDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.appointment.constant.ErrorCodeConstant.ERR_ACCOUNT_LOCKED;
import static com.appointment.constant.ErrorCodeConstant.ERR_PASSWORD_EXPIRED;
import static com.appointment.constant.ErrorCodeConstant.ERR_USER_LOCKED;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEventHandler {

    private final UserRepository userRepository;
    private final ApplicationProperties applicationProperties;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        
        if (authentication.getPrincipal() instanceof CustomUserDetail userDetail) {
            log.info("Authentication successful for user: {}", userDetail.getUsername());

            if (userDetail.isUserBlocked()) {
                log.warn("Authentication failed - user blocked: {}", userDetail.getUsername());
                throw new LockedException(ERR_USER_LOCKED);
            }

            if (userDetail.isAccountLocked()) {
                log.warn("Authentication failed - account locked: {}", userDetail.getUsername());
                throw new LockedException(ERR_ACCOUNT_LOCKED);
            }

            // Reset failed login attempts on successful authentication
            Optional<User> userOpt = userRepository.findByEmployeeIdAndDeletedFalse(userDetail.getUsername());
            if (userOpt.isEmpty()) {
                return;
            }

            User user = userOpt.get();
            user.resetFailedLoginAttempts();
            userRepository.save(user);
            log.debug("Failed login attempts reset for user: {}", userDetail.getUsername());

            // Check if password has expired
            if (userDetail.isPasswordExpired()) {
                log.warn("Authentication failed - password expired: {}", userDetail.getUsername());
                throw new BadRequestException(ERR_PASSWORD_EXPIRED);
            }
        }
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = BadRequestException.class)
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        String employeeId = event.getAuthentication().getName();
        String failureReason = event.getException().getMessage();
        
        log.warn("Authentication failed for user: {} - {}", employeeId, failureReason);
        // Find user and record failed attempt
        Optional<User> userOpt = userRepository.findByEmployeeIdAndDeletedFalse(employeeId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.incrementFailedLoginAttempts();

            // Lock account if max attempts reached
            if (user.getFailedLoginAttempts() >= applicationProperties.getSecurity().getLogin().getMaxAttempts()) {
                user.lockAccount(applicationProperties.getSecurity().getLogin().getLockoutMinutes());
                log.warn("Account locked for user: {} due to {} failed attempts", user.getEmail(), user.getFailedLoginAttempts());
                throw  new BadRequestException(ERR_ACCOUNT_LOCKED);
            }

            userRepository.save(user);
            log.debug("Failed login attempt recorded for user: {}", employeeId);
        } else {
            log.warn("Failed login attempt for non-existent user: {}", employeeId);
        }
    }
}
