package com.unicloudapp.management.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
record CloudResourceAccessClientRowView(
        String CloudAccessClientId,
        String CloudResourceAccessClientName,
        BigDecimal costLimit,
        String defaultCronExpression
) {

}

@Builder
record CloudResourceAccessClientDetails(
        String CloudAccessClientId,
        String CloudResourceAccessClientName,
        BigDecimal costLimit,
        String defaultCronExpression,
        boolean isActive
) {}
