package com.appointment.service.impl;

import com.appointment.repository.UserRepository;
import com.appointment.service.mapper.AuthenticationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("customUserDetailService")
@RequiredArgsConstructor
public class CustomUserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthenticationMapper authMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String employeeId) throws UsernameNotFoundException {
        return userRepository.findByEmployeeIdAndDeletedFalse(employeeId)
                .map(authMapper::toUserDetail)
                .orElseThrow(() -> {
                    log.warn("User not found by employee id: {}", employeeId);
                    return new UsernameNotFoundException("User not found by employee id");
                });
    }
}
