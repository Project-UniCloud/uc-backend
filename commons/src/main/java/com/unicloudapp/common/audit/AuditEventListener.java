package com.unicloudapp.common.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventListener {

    private final AuditLogRepository auditLogRepository;

    @EventListener
    @Transactional
    public void handleAuditEvent(AuditEvent event) {
        log.info("Audit event received: {} by {}", event.action(), event.actor());
        AuditLogEntity entity = AuditLogEntity.builder()
                .action(event.action())
                .actor(event.actor())
                .occurredAt(event.occurredAt())
                .details(event.details())
                .build();
        auditLogRepository.save(entity);
    }
}
