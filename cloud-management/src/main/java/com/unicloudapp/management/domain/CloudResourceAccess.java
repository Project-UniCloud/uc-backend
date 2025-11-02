package com.unicloudapp.management.domain;

import com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto;
import com.unicloudapp.common.vo.cloud.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.scheduling.support.CronExpression;

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

    public void updateUsedLimit(UsedLimit newUsedCost) {
        if (newUsedCost == null || newUsedCost.getValue().intValue() < usedLimit.getValue().intValue()) {
            throw new IllegalArgumentException("New used limit cannot be null");
        }
        this.usedLimit = newUsedCost;
    }

    public void active() {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.ACTIVE);
    }

    public void deactivate() {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.status = CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.INACTIVE);
    }

    public void update(CloudResourceAccessDetailsDto request) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        this.costLimit = CostLimit.of(request.limit());
        this.cronExpression = CronExpression.parse(request.cron());
        this.expiresAt = ExpiresDate.of(request.expiresAt());
    }
}
