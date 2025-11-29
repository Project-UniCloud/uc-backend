package com.unicloudapp.management.domain;

import com.unicloudapp.common.vo.cloud.*;
import org.springframework.scheduling.support.CronExpression;

public class CloudResourceAccessFactory {

    public CloudResourceAccess create(
            CloudResourceAccessId cloudResourceAccessId,
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType cloudResourceType,
            CostLimit costLimit,
            CronExpression cronExpression,
            ExpiresDate expiresAt
    ) {
        if (!isValid(cloudResourceAccessId, cloudAccessClientId, cloudResourceType, costLimit)) {
            throw new IllegalArgumentException("Invalid parameters for creating CloudResourceAccess");
        }
        return new CloudResourceAccess(
                cloudResourceAccessId,
                cloudAccessClientId,
                cloudResourceType,
                costLimit,
                UsedLimit.empty(),
                cronExpression,
                expiresAt,
                CloudResourcesAccessStatus.of(CloudResourcesAccessStatus.Status.INACTIVE),
                NotificationLevel.of(50),
                NotificationLevel.of(80),
                NotificationLevel.of(95)
        );
    }

    private boolean isValid(
            CloudResourceAccessId cloudResourceAccessId,
            CloudAccessClientId cloudAccessClientId,
            CloudResourceType cloudResourceType,
            CostLimit costLimit
    ) {
        return cloudResourceAccessId != null &&
                cloudAccessClientId != null &&
                cloudResourceType != null &&
                costLimit != null;
    }
}
