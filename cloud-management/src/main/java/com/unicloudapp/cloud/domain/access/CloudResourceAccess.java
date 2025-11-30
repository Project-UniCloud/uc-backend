package com.unicloudapp.cloud.domain.access;

import com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto;
import com.unicloudapp.common.vo.cloud.*;
import com.unicloudapp.cloud.domain.ExpiresDate;
import com.unicloudapp.cloud.domain.NotificationLevel;
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
    private final CloudVendorConnectorId cloudVendorConnectorId;
    private final CloudResourceType cloudResourceType;
    private CostLimit costLimit;
    private UsedLimit usedLimit;
    private CronExpression cronExpression;
    private ExpiresDate expiresAt;
    private CloudResourcesAccessStatus status;
    private NotificationLevel notificationLevel1;
    private NotificationLevel notificationLevel2;
    private NotificationLevel notificationLevel3;

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
        this.notificationLevel1 = NotificationLevel.of(request.notificationLevel1());
        this.notificationLevel2 = NotificationLevel.of(request.notificationLevel2());
        this.notificationLevel3 = NotificationLevel.of(request.notificationLevel3());
    }
}
