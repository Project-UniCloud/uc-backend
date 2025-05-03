package com.unicloudapp.cloudmanagment.infrastructure.persistence;

import com.unicloudapp.cloudmanagment.domain.CloudAccess;
import com.unicloudapp.cloudmanagment.domain.CloudAccessId;
import com.unicloudapp.cloudmanagment.domain.ExternalUserId;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import org.springframework.stereotype.Component;

@Component
class CloudAccessMapper {

    CloudAccess toDomain(CloudAccessEntity entity) {
        return CloudAccess.builder()
                .cloudAccessId(CloudAccessId.of(entity.getCloudAccessId()))
                .cloudAccessClientId(CloudAccessClientId.of(entity.getCloudAccessClientId()))
                .userId(UserId.of(entity.getUserId()))
                .externalUserId(ExternalUserId.of(entity.getExternalUserId()))
                .build();
    }

    CloudAccessEntity toEntity(CloudAccess domain) {
        return CloudAccessEntity.builder()
                .cloudAccessId(domain.getCloudAccessId().getValue())
                .cloudAccessClientId(domain.getCloudAccessClientId().getValue())
                .userId(domain.getUserId().getValue())
                .externalUserId(domain.getExternalUserId().getUserId())
                .build();
    }

}
