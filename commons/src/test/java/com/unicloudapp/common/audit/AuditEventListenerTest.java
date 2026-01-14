package com.unicloudapp.common.audit;

import static org.mockito.Mockito.verify;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuditEventListenerTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditEventListener auditEventListener;

    @BeforeEach
    void setUp() {
        auditEventListener = new AuditEventListener(auditLogRepository);
    }

    @Test
    void shouldHandleAuditEventAndSaveToRepository() {
        // given
        Instant now = Instant.now();
        AuditEvent event = AuditEvent.builder()
                .action("TEST_ACTION")
                .actor("test-user")
                .occurredAt(now)
                .details(Map.of("key", "value"))
                .build();

        // when
        auditEventListener.handleAuditEvent(event);

        // then
        ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLogEntity savedEntity = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("TEST_ACTION", savedEntity.getAction());
        org.junit.jupiter.api.Assertions.assertEquals("test-user", savedEntity.getActor());
        org.junit.jupiter.api.Assertions.assertEquals(now, savedEntity.getOccurredAt());
        org.junit.jupiter.api.Assertions.assertEquals(Map.of("key", "value"), savedEntity.getDetails());
    }

    @Test
    void shouldHandleAuditEventWithNullDetails() {
        // given
        Instant now = Instant.now();
        AuditEvent event = AuditEvent.builder()
                .action("TEST_ACTION_NULL_DETAILS")
                .actor("test-user")
                .occurredAt(now)
                .details(null)
                .build();

        // when
        auditEventListener.handleAuditEvent(event);

        // then
        ArgumentCaptor<AuditLogEntity> captor = ArgumentCaptor.forClass(AuditLogEntity.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLogEntity savedEntity = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("TEST_ACTION_NULL_DETAILS", savedEntity.getAction());
        org.junit.jupiter.api.Assertions.assertNull(savedEntity.getDetails());
    }
}
