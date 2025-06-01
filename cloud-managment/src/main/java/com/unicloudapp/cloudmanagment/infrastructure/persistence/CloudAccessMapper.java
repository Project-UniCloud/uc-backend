package com.unicloudapp.cloudmanagment.infrastructure.persistence;

import com.unicloudapp.cloudmanagment.domain.CloudResourceAccess;
import com.unicloudapp.cloudmanagment.domain.CostLimit;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import org.springframework.stereotype.Component;

@Component
class CloudAccessMapper {

    CloudResourceAccess toDomain(CloudResourceAccessEntity entity) {
        return CloudResourceAccess.builder()
                .cloudResourceAccessId(CloudResourceAccessId.of(entity.getCloudResourceAccessId()))
                .cloudAccessClientId(CloudAccessClientId.of(entity.getCloudAccessClientId()))
                .cloudResourceType(CloudResourceType.of(entity.getResourceType()))
                .costLimit(CostLimit.of(entity.getCostLimit()))
                .build();
    }

    CloudResourceAccessEntity toEntity(CloudResourceAccess domain) {
        return CloudResourceAccessEntity.builder()
                .cloudResourceAccessId(domain.getCloudResourceAccessId()
                        .getValue())
                .cloudAccessClientId(domain.getCloudAccessClientId().getValue())
                .resourceType(domain.getCloudResourceType().getName())
                .costLimit(domain.getCostLimit().getCost())
                .build();
    }

}
