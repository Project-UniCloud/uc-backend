package com.unicloudapp.common.cloud.event;

import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import java.time.Instant;
import lombok.Builder;

@Builder
public record CloudBudgetThresholdExceededEvent(
        CloudResourceAccessId cloudResourceAccessId,
        int notificationLevel,
        UsedLimit limit,
        CostLimit costLimit,
        Instant occurredAt) {}
