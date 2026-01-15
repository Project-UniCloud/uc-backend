package com.unicloudapp.common.audit.infrastructure.rest;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;

@Builder
public record AuditLogResponse(Long id, String action, String actor, Instant occurredAt, Map<String, String> details) {}
