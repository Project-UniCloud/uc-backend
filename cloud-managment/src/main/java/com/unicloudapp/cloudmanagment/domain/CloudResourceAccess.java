package com.unicloudapp.cloudmanagment.domain;

import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDate;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@Getter
public class CloudResourceAccess {

    private final CloudResourceAccessId cloudResourceAccessId;
    private final CloudAccessClientId cloudAccessClientId;
    private final CloudResourceType cloudResourceType;
    private CostLimit costLimit;
    private UsedLimit usedLimit;
    private CronExpression cronExpression;
    private ExpiresDate expiresAt;
    private CloudResourcesAccessStatus status;

    void changeCostLimit(CostLimit newCostLimit) {
        if (newCostLimit == null) {
            throw new IllegalArgumentException("New cost limit cannot be null");
        }
        this.costLimit = newCostLimit;
    }
}
