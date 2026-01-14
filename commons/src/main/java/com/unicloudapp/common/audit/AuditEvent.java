package com.unicloudapp.common.audit;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;

@Builder
public record AuditEvent(String action, String actor, Instant occurredAt, Map<String, String> details) {
    public static AuditEvent of(String action, String actor, Map<String, String> details) {
        return AuditEvent.builder()
                .action(action)
                .actor(actor)
                .occurredAt(Instant.now())
                .details(details)
                .build();
    }
}
