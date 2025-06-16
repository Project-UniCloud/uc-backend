package com.unicloudapp.cloudmanagment.infrastructure.rest;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

record CloudResourceNameResponse(
        UUID cloudResourceAccessId,
        String cloudResourceName
) {

}

@Builder
record CloudAccessClientRowView(
        String cloudAccessClientId,
        String cloudAccessClientName,
        BigDecimal costLimit,
        String defaultCronExpression
) {

}
