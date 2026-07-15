package com.appointment.repository;

import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Resource lock repository contracts")
class ResourceLockRepositoryContractTest {

    @Test
    @DisplayName("Technician findAvailableForUpdate uses PESSIMISTIC_WRITE")
    void technicianLockIsPessimisticWrite() throws Exception {
        Method method = TechnicianRepository.class.getMethod("findAvailableForUpdate");
        Lock lock = method.getAnnotation(Lock.class);
        Query query = method.getAnnotation(Query.class);

        assertThat(lock).isNotNull();
        assertThat(lock.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
        assertThat(query).isNotNull();
        assertThat(query.value()).containsIgnoringCase("AVAILABLE");
    }

    @Test
    @DisplayName("ServiceBay findAvailableForUpdate uses PESSIMISTIC_WRITE")
    void serviceBayLockIsPessimisticWrite() throws Exception {
        Method method = ServiceBayRepository.class.getMethod("findAvailableForUpdate");
        Lock lock = method.getAnnotation(Lock.class);
        Query query = method.getAnnotation(Query.class);

        assertThat(lock).isNotNull();
        assertThat(lock.value()).isEqualTo(LockModeType.PESSIMISTIC_WRITE);
        assertThat(query).isNotNull();
        assertThat(query.value()).containsIgnoringCase("AVAILABLE");
    }
}
