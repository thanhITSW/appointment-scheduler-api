package com.appointment.config;

import com.appointment.constant.CommonConstant;
import com.appointment.enumeration.UserRole;
import com.appointment.security.filter.JwtAuthenticationFilter;
import com.appointment.security.handler.LogoutHandlerEvent;
import com.appointment.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Objects;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsService customUserDetailService;
    private final LogoutHandlerEvent logoutHandlerEvent;
    private final ApplicationProperties applicationConfig;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) {

        try {
            return http.getSharedObject(AuthenticationManagerBuilder.class)
                    .authenticationProvider(daoAuthentication()).build();
        } catch (Exception e) {
            return null;
        }
    }

    @Bean
    @SuppressWarnings("deprecation")
    AuthenticationProvider daoAuthentication() {
        // Using deprecated constructor and methods for backward compatibility
        // Note: DaoAuthenticationProvider is deprecated in Spring Security 6.x but still functional
        DaoAuthenticationProvider daoAuthProvider = new DaoAuthenticationProvider();
        daoAuthProvider.setUserDetailsService(customUserDetailService);
        daoAuthProvider.setPasswordEncoder(bcryptPasswordEncoder());
        daoAuthProvider.setHideUserNotFoundExceptions(false);
        return daoAuthProvider;
    }

    @Bean
    PasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(authz -> authz.requestMatchers(
                "/api/v1/public/auth/login",
                "/api/v1/public/auth/refresh",
                "/swagger-ui/**",
                "/api-docs/**",
                "/swagger",
                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/api/v1/private/users/**").hasRole(UserRole.ADMIN.name())
                        .requestMatchers("/api/v1/private/technicians/**",
                                "/api/v1/private/service-bays/**",
                                "/api/v1/private/service-types/**",
                                "/api/v1/private/skills/**")
                            .hasAnyRole(UserRole.MANAGER.name(), UserRole.ADMIN.name())
                        .requestMatchers("/api/v1/private/appointments/**",
                                "/api/v1/private/customers/**")
                            .hasAnyRole(UserRole.ADVISOR.name(), UserRole.MANAGER.name(), UserRole.ADMIN.name())
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers("/api/v1/private/**").authenticated()
                        .requestMatchers("/api/v1/public/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(daoAuthentication())
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter(), LogoutFilter.class)
                .logout(logoutHandler -> logoutHandler.logoutUrl("/api/v1/public/auth/logout")
                        .addLogoutHandler(logoutHandlerEvent)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                        .deleteCookies(CommonConstant.TOKEN_COOKIE_NAME))
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Access Denied\",\"message\":\"Insufficient permissions\"}");
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Authentication required\"}");
                        }))
                .build();
    }

    @Bean
    CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = applicationConfig.getSecurity().getCors();
        log.info("CORS filter initialized {}", configuration.getAllowedOrigins());
        if (Objects.nonNull(configuration.getAllowedOrigins())) {
            log.debug("Registering CORS filter");
            source.registerCorsConfiguration("/**", configuration);
        }
        return new CorsFilter(source);
    }
}
