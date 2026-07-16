package com.appointment.jpa;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.appointment.utils.SecurityUtils;
import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS;

/**
 * Implementation of {@link AuditorAware} based on Spring Security.
 */
@Component("springSecurityAuditorAware")
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    private static final String SYSTEM = "SYSTEM";

    @Nonnull
    @Override
    public Optional<String> getCurrentAuditor() {
        final String currentAuditor = SecurityUtils
                .getCurrentUserLogin()
                .map(currentUserLogin -> currentUserLogin.getUserId().toString())
                .orElse(SYSTEM);

        return Optional.of(currentAuditor);
    }

    @Bean
    public Hibernate6Module hibernate6Module() {
        return new Hibernate6Module().configure(SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
    }
}
