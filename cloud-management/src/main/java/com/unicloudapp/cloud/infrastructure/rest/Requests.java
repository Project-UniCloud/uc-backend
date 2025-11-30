package com.unicloudapp.cloud.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
record CloudConnectorSaveRequestDto(
        String cloudConnectorId,
        String host,
        Integer port,
        BigDecimal defaultCostLimit,
        String cronExpression,
        String name
) { }

record PatchCloudConnectorRequestDto(
        String cloudConnectorId,
        List<String> resourceTypes
) {}