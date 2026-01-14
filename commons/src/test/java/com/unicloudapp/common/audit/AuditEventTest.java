package com.unicloudapp.common.audit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;

class AuditEventTest {

    @Test
    void shouldCreateAuditEventUsingOfFactoryMethod() {
        // given
        String action = "USER_LOGIN";
        String actor = "john.doe";
        Map<String, String> details = Map.of("ip", "127.0.0.1");

        // when
        AuditEvent event = AuditEvent.of(action, actor, details);

        // then
        assertThat(event.action()).isEqualTo(action);
        assertThat(event.actor()).isEqualTo(actor);
        assertThat(event.details()).isEqualTo(details);
        assertThat(event.occurredAt()).isBeforeOrEqualTo(Instant.now());
    }

    @Test
    void shouldCreateAuditEventUsingBuilder() {
        // given
        String action = "USER_LOGOUT";
        String actor = "jane.doe";
        Instant now = Instant.now();
        Map<String, String> details = Map.of("reason", "timeout");

        // when
        AuditEvent event = AuditEvent.builder()
                .action(action)
                .actor(actor)
                .occurredAt(now)
                .details(details)
                .build();

        // then
        assertThat(event.action()).isEqualTo(action);
        assertThat(event.actor()).isEqualTo(actor);
        assertThat(event.details()).isEqualTo(details);
        assertThat(event.occurredAt()).isEqualTo(now);
    }
}
