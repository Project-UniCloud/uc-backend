package com.unicloudapp.cloud.domain.access;

import com.unicloudapp.cloud.domain.vo.CloudResourcesAccessStatus;
import com.unicloudapp.common.vo.cloud.*;
import com.unicloudapp.cloud.domain.vo.ExpiresDate;
import com.unicloudapp.cloud.domain.vo.NotificationLevel;
import org.springframework.scheduling.support.CronExpression;

public class CloudResourceAccessFactory {

    public CloudResourceAccess create(
            CloudResourceAccessId cloudResourceAccessId,
            CloudConnectorId cloudConnectorId,
            CloudResourceType cloudResourceType,
            CostLimit costLimit,
            CronExpression cronExpression,
            ExpiresDate expiresAt
    ) {
        if (!isValid(cloudResourceAccessId, cloudConnectorId, cloudResourceType, costLimit)) {
            throw new IllegalArgumentException("Invalid parameters for creating CloudResourceAccess");
        }
        return CloudResourceAccess.builder()
                .cloudResourceAccessId(cloudResourceAccessId)
                .cloudConnectorId(cloudConnectorId)
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
            CloudConnectorId cloudConnectorId,
            CloudResourceType cloudResourceType,
            CostLimit costLimit
    ) {
        return cloudResourceAccessId != null &&
                cloudConnectorId != null &&
                cloudResourceType != null &&
                costLimit != null;
    }
}
