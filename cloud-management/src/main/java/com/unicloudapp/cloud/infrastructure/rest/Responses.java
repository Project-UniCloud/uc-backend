package com.unicloudapp.cloud.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
record CloudResourceAccessClientRowView(
        String CloudVendorConnectorId,
        String CloudResourceAccessClientName,
        BigDecimal costLimit,
        String defaultCronExpression
) {

}

@Builder
record CloudResourceAccessClientDetails(
        String CloudVendorConnectorId,
        String CloudResourceAccessClientName,
        BigDecimal costLimit,
        String defaultCronExpression,
        boolean isActive
) {}
