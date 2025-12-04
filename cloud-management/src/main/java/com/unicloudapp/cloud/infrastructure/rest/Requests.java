package com.unicloudapp.cloud.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
record CloudConnectorSaveRequestDto(
        String cloudConnectorId,
        String host,
        Integer port,
        BigDecimal defaultCostLimit,
        String cronExpression,
        String name
) { }

record CloudConnectorResourceTypeRequestDto(
        String cloudConnectorId,
        String resourceType
) {}