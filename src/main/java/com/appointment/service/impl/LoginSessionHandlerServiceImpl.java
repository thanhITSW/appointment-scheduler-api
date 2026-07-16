package com.appointment.service.impl;

import com.appointment.config.ApplicationProperties;
import com.appointment.entity.User;
import com.appointment.exception.DataNotfoundException;
import com.appointment.repository.LoginSessionRepository;
import com.appointment.repository.UserRepository;
import com.appointment.security.jwt.JwtTokenPayload;
import com.appointment.service.LoginSessionHandlerService;
import com.appointment.service.dto.request.LoginSessionRequestDto;
import com.appointment.service.mapper.LoginSessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.appointment.constant.ErrorCodeConstant.ERR_USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginSessionHandlerServiceImpl implements LoginSessionHandlerService {
    private final LoginSessionRepository loginSessionRepository;
    private final UserRepository userRepository;

    private final LoginSessionMapper loginSessionMapper;

    private final ApplicationProperties properties;

    @Transactional
    @Override
    public void saveLoginSession(LoginSessionRequestDto request) {
        User user = userRepository.findByIdAndDeletedFalse(request.getUserId())
                .orElseThrow(() -> {
                    log.warn("User not found by id {}", request.getUserId());
                    return new DataNotfoundException(ERR_USER_NOT_FOUND);
                });
        Optional.of(loginSessionMapper.toEntity(request))
                .ifPresent(req -> {
                    user.addLoginSession(req);
                    loginSessionRepository.save(req);
                });
    }


    @Transactional
    @Override
    public void inactiveUserSession(Long userId, String sessionId) {
        loginSessionRepository.inactiveSession(userId, sessionId);
    }

    @Override
    @Transactional
    public Optional<JwtTokenPayload> extractCacheLoginSession(String sessionId) {
        try {
            // Look up session by JTI to validate it exists and is active
            return loginSessionRepository.findByActiveSession(sessionId)
                    .filter(loginSession -> !loginSession.isExpired())
                    .map(JwtTokenPayload::buildFormLoginSession);
        } catch (Exception e) {
            log.warn("Failed to extract session from session: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public void renewExpiredSession(LoginSessionRequestDto sessionDto) {
        loginSessionRepository.findByActiveSession(sessionDto.getSessionId(), sessionDto.getUserId())
                .ifPresentOrElse(
                        loginSession -> loginSession.setExpiresAt(sessionDto.getExpiresAt())
                        , () -> {
                    log.warn("Session not found by sessionId {} - userId {}", sessionDto.getSessionId(),
                            sessionDto.getUserId());
                    throw new DataNotfoundException("error.session.notfound");
                });

    }
}
