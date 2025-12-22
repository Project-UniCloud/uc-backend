package com.unicloudapp.common.cloud.event;

import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import lombok.Builder;

import java.time.Instant;

@Builder
public record CloudBudgetThresholdExceededEvent(
        CloudResourceAccessId cloudResourceAccessId,
        int notificationLevel,
        UsedLimit limit,
        CostLimit costLimit,
        Instant occurredAt
) {
}
