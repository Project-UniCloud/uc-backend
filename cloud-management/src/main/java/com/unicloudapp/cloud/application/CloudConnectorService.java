package com.unicloudapp.cloud.application;

import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Transactional
public class CloudConnectorService {

    private final ApplicationEventPublisher eventPublisher;
    private final CloudConnectorRepositoryPort cloudConnectorRepositoryPort;

    @Transactional
    public void createConnector(
        CloudConnectorId cloudConnectorId,
        String host,
        Integer port,
        CostLimit costLimit,
        CronExpression cronExpression,
        String name
    ) {
        val cloudConnector = CloudConnector.builder()
                .cloudConnectorId(cloudConnectorId)
                .host(host)
                .port(port)
                .defaultCostLimit(costLimit)
                .cronExpression(cronExpression)
                .name(name)
                .resourceTypes(Collections.emptyList())
                .build();
        if (cloudConnectorRepositoryPort.findByClientId(cloudConnector.getCloudConnectorId()).isPresent()) {
            throw new IllegalArgumentException("CloudVendorConnectorId " + cloudConnector.getCloudConnectorId() + " already exists");
        }
        cloudConnectorRepositoryPort.save(cloudConnector);
        eventPublisher.publishEvent(new CloudConnectorCreatedEvent(cloudConnector.getCloudConnectorId(), host, port));
    }

    @Transactional
    public void deleteResourceType(CloudConnectorId cloudConnectorId, CloudResourceType resourceType) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort
                .findByClientId(cloudConnectorId)
                .orElseThrow();
        cloudConnector.deleteResourceType(resourceType);
        cloudConnectorRepositoryPort.save(cloudConnector);
    }

    @Transactional
    public void addResourceType(CloudConnectorId cloudConnectorId, CloudResourceType resourceType) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort
                .findByClientId(cloudConnectorId)
                .orElseThrow();
        cloudConnector.addResourceType(resourceType);
        cloudConnectorRepositoryPort.save(cloudConnector);
    }

    public record CloudConnectorCreatedEvent(CloudConnectorId cloudConnectorId, String host, Integer port) {}
}
