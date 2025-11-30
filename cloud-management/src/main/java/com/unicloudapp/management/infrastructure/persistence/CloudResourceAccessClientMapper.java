package com.unicloudapp.management.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.management.domain.access_client.CloudResourceAccessClient;
import org.mapstruct.Mapper;
import org.springframework.scheduling.support.CronExpression;

@Mapper(componentModel = "spring")
public interface CloudResourceAccessClientMapper {

    default CloudAccessClientEntity toEntity(CloudResourceAccessClient domain) {
        if (domain == null) return null;
        return CloudAccessClientEntity.builder()
                .CloudAccessClientId(domain.getCloudAccessClientId() != null ? domain.getCloudAccessClientId().getValue() : null)
                .name(domain.getName())
                .host(domain.getHost())
                .port(parsePort(domain.getPort()))
                .defaultCostLimit(domain.getDefaultCostLimit() != null ? domain.getDefaultCostLimit().getCost() : null)
                .defaultCleanUpCron(domain.getCronExpression() != null ? domain.getCronExpression().toString() : null)
                .resourceTypes(domain.getResourceTypes() != null ? domain.getResourceTypes().stream().map(CloudResourceType::getName).toList() : null)
                .build();
    }

    default CloudResourceAccessClient toDomain(CloudAccessClientEntity entity) {
        if (entity == null) return null;
        return CloudResourceAccessClient.builder()
                .CloudAccessClientId(entity.getCloudAccessClientId() != null ? CloudAccessClientId.of(entity.getCloudAccessClientId()) : null)
                .name(entity.getName())
                .host(entity.getHost())
                .port(entity.getPort() != null ? entity.getPort().toString() : null)
                .defaultCostLimit(entity.getDefaultCostLimit() != null ? CostLimit.of(entity.getDefaultCostLimit()) : null)
                .cronExpression(entity.getDefaultCleanUpCron() != null ? CronExpression.parse(entity.getDefaultCleanUpCron()) : null)
                .resourceTypes(entity.getResourceTypes() != null ? entity.getResourceTypes().stream().map(CloudResourceType::of).toList() : null)
                .controller(null)
                .CloudResourceAccessFactory(null)
                .build();
    }

    private static Integer parsePort(String port) {
        if (port == null) return null;
        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
