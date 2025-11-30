package com.unicloudapp.cloud.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.CloudVendorConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.cloud.domain.vendor_connector.CloudVendorConnector;
import org.mapstruct.Mapper;
import org.springframework.scheduling.support.CronExpression;

@Mapper(componentModel = "spring")
public interface CloudResourceAccessClientMapper {

    default CloudAccessClientEntity toEntity(CloudVendorConnector domain) {
        if (domain == null) return null;
        return CloudAccessClientEntity.builder()
                .CloudVendorConnectorId(domain.getCloudVendorConnectorId() != null ? domain.getCloudVendorConnectorId().id() : null)
                .name(domain.getName())
                .host(domain.getHost())
                .port(parsePort(domain.getPort()))
                .defaultCostLimit(domain.getDefaultCostLimit() != null ? domain.getDefaultCostLimit().getCost() : null)
                .defaultCleanUpCron(domain.getCronExpression() != null ? domain.getCronExpression().toString() : null)
                .resourceTypes(domain.getResourceTypes() != null ? domain.getResourceTypes().stream().map(CloudResourceType::getName).toList() : null)
                .build();
    }

    default CloudVendorConnector toDomain(CloudAccessClientEntity entity) {
        if (entity == null) return null;
        return CloudVendorConnector.builder()
                .cloudVendorConnectorId(entity.getCloudVendorConnectorId() != null ? CloudVendorConnectorId.of(entity.getCloudVendorConnectorId()) : null)
                .name(entity.getName())
                .host(entity.getHost())
                .port(entity.getPort() != null ? entity.getPort().toString() : null)
                .defaultCostLimit(entity.getDefaultCostLimit() != null ? CostLimit.of(entity.getDefaultCostLimit()) : null)
                .cronExpression(entity.getDefaultCleanUpCron() != null ? CronExpression.parse(entity.getDefaultCleanUpCron()) : null)
                .resourceTypes(entity.getResourceTypes() != null ? entity.getResourceTypes().stream().map(CloudResourceType::of).toList() : null)
                .controller(null)
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
