package com.unicloudapp.cloud.infrastructure.rest;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
record CloudConnectorRowView(
        String cloudConnectorId, String cloudConnectorName, BigDecimal costLimit, String defaultCronExpression) {}

@Builder
record CloudConnectorDetailsDto(
        String cloudConnectorId,
        String cloudConnectorName,
        BigDecimal costLimit,
        String defaultCronExpression,
        boolean isActive) {}
