package com.unicloudapp.cloud.infrastructure.persistence;

import com.unicloudapp.cloud.domain.vo.ExpiresDate;
import com.unicloudapp.common.vo.cloud.*;
import com.unicloudapp.cloud.domain.access.CloudResourceAccess;
import com.unicloudapp.cloud.domain.vo.CloudResourcesAccessStatus;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

@Component
class CloudResourceAccessMapper {

    CloudResourceAccess toDomain(CloudResourceAccessEntity entity) {
        return CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(entity.getCloudResourceAccessId()))
                .cloudConnectorId(CloudConnectorId.of(entity.getCloudConnectorId()))
                .cloudResourceType(CloudResourceType.of(entity.getResourceType()))
                .costLimit(CostLimit.of(entity.getCostLimit()))
                .usedLimit(UsedLimit.of(entity.getUsedLimit()))
                .expiresAt(ExpiresDate.expirable(entity.getExpiresAt()))
                .status(CloudResourcesAccessStatus.of(entity.getStatus()))
                .cronExpression(CronExpression.parse(entity.getCronExpression()))
                .build();
    }

    CloudResourceAccessEntity toEntity(CloudResourceAccess domain) {
        return CloudResourceAccessEntity.builder()
                .cloudResourceAccessId(domain.getCloudResourceAccessId().getValue())
                .cloudConnectorId(domain.getCloudConnectorId().id())
                .resourceType(domain.getCloudResourceType().getName())
                .costLimit(domain.getCostLimit().getCost())
                .cronExpression(domain.getCronExpression().toString())
                .usedLimit(domain.getUsedLimit().getValue())
                .expiresAt(domain.getExpiresAt().getValue())
                .status(domain.getStatus().getStatus())
                .notificationLevel1(domain.getNotificationLevel1().level())
                .notificationLevel2(domain.getNotificationLevel2().level())
                .notificationLevel3(domain.getNotificationLevel3().level())
                .build();
    }

}
