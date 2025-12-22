package com.unicloudapp.cloud.infrastructure.rest;

import java.math.BigDecimal;
import lombok.Builder;

@Builder
record CloudConnectorSaveRequestDto(
        String cloudConnectorId,
        String host,
        Integer port,
        BigDecimal defaultCostLimit,
        String cronExpression,
        String name) {}
