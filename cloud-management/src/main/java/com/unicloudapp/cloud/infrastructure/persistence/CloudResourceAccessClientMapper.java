package com.unicloudapp.cloud.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import org.mapstruct.Mapper;
import org.springframework.scheduling.support.CronExpression;

@Mapper(componentModel = "spring")
interface CloudResourceAccessClientMapper {

    default CloudConnectorEntity toEntity(CloudConnector domain) {
        if (domain == null) return null;
        return CloudConnectorEntity.builder()
                .id(domain.getCloudConnectorId() != null ? domain.getCloudConnectorId().id() : null)
                .name(domain.getName())
                .host(domain.getHost())
                .port(domain.getPort())
                .defaultCostLimit(domain.getDefaultCostLimit() != null ? domain.getDefaultCostLimit().getCost() : null)
                .defaultCleanUpCron(domain.getCronExpression() != null ? domain.getCronExpression().toString() : null)
                .resourceTypes(domain.getResourceTypes() != null ? domain.getResourceTypes().stream().map(CloudResourceType::getName).toList() : null)
                .build();
    }

    default CloudConnector toDomain(CloudConnectorEntity entity) {
        if (entity == null) return null;
        return CloudConnector.builder()
                .cloudConnectorId(entity.getId() != null ? CloudConnectorId.of(entity.getId()) : null)
                .name(entity.getName())
                .host(entity.getHost())
                .port(entity.getPort())
                .defaultCostLimit(entity.getDefaultCostLimit() != null ? CostLimit.of(entity.getDefaultCostLimit()) : null)
                .cronExpression(entity.getDefaultCleanUpCron() != null ? CronExpression.parse(entity.getDefaultCleanUpCron()) : null)
                .resourceTypes(entity.getResourceTypes() != null ? entity.getResourceTypes().stream().map(CloudResourceType::of).toList() : null)
                .build();
    }
}
