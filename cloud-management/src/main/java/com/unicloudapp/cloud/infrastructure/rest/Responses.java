package com.unicloudapp.cloud.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
record CloudConnectorRowView(
        String cloudVendorConnectorId,
        String cloudResourceAccessClientName,
        BigDecimal costLimit,
        String defaultCronExpression
) {

}

@Builder
record CloudConnectorDetailsDto(
        String cloudConnectorId,
        String name,
        BigDecimal costLimit,
        String defaultCronExpression,
        boolean isActive
) {}
