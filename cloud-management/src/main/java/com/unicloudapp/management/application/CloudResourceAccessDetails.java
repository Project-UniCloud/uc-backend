package com.unicloudapp.management.application;

import java.math.BigDecimal;

public record CloudResourceAccessDetails(
        String client,
        String resourceTypeName,
        BigDecimal costLimit,
        BigDecimal cost,
        String region,
        String accountId
) {

}
