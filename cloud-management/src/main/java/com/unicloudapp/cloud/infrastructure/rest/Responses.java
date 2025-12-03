package com.unicloudapp.cloud.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Builder
record CloudConnectorRowView(
        String cloudConnectorId,
        String cloudConnectorName,
        BigDecimal costLimit,
        String defaultCronExpression
) {

}

@Builder
record CloudConnectorDetailsDto(
        String cloudConnectorId,
        String cloudConnectorName,
        BigDecimal costLimit,
        String defaultCronExpression,
        boolean isActive
) {}
