package com.unicloudapp.management.domain.access;

import com.unicloudapp.common.vo.cloud.*;
import com.unicloudapp.management.domain.ExpiresDate;
import com.unicloudapp.management.domain.NotificationLevel;
import org.springframework.scheduling.support.CronExpression;

public class CloudResourceAccessFactory {

    public CloudResourceAccess create(
            CloudResourceAccessId cloudResourceAccessId,
            CloudAccessClientId CloudAccessClientId,
            CloudResourceType cloudResourceType,
            CostLimit costLimit,
            CronExpression cronExpression,
            ExpiresDate expiresAt
    ) {
        if (!isValid(cloudResourceAccessId, CloudAccessClientId, cloudResourceType, costLimit)) {
            throw new IllegalArgumentException("Invalid parameters for creating CloudResourceAccess");
        }
        return CloudResourceAccess.builder()
                .cloudResourceAccessId(cloudResourceAccessId)
                .cloudAccessClientId(CloudAccessClientId)
                .cloudResourceType(cloudResourceType)
                .costLimit(costLimit)
                .usedLimit(UsedLimit.empty())
                .cronExpression(cronExpression)
                .expiresAt(expiresAt)
                .status(CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.INACTIVE))
                .notificationLevel1(NotificationLevel.of(50))
                .notificationLevel2(NotificationLevel.of(80))
                .notificationLevel3(NotificationLevel.of(95))
                .build();
    }

    private boolean isValid(
            CloudResourceAccessId cloudResourceAccessId,
            CloudAccessClientId CloudAccessClientId,
            CloudResourceType cloudResourceType,
            CostLimit costLimit
    ) {
        return cloudResourceAccessId != null &&
                CloudAccessClientId != null &&
                cloudResourceType != null &&
                costLimit != null;
    }
}
