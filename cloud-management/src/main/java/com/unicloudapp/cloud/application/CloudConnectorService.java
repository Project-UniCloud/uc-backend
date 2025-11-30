package com.unicloudapp.cloud.application;

import com.unicloudapp.cloud.application.port.CloudConnectorClientFactoryPort;
import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CloudConnectorService {

    private final CloudConnectorRepositoryPort cloudConnectorRepositoryPort;

    @Transactional
    public void createConnector(

    ) {
        val cloudConnector = CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of("aws"))
                .host("localhost")
                .port(0)
                .defaultCostLimit(BigDecimal.ONE)
                .cronExpression("")
                .name("")
                .resourceTypes(List.of())
                .build();
        if (cloudConnectorRepositoryPort.findByClientId(cloudConnector.getCloudConnectorId()).isPresent()) {
            throw new IllegalArgumentException("CloudVendorConnectorId " + cloudConnector.getCloudConnectorId() + " already exists");
        }
        cloudConnectorRepositoryPort.save(cloudConnector);
    }

    @Transactional
    public void addResourceType(CloudConnectorId cloudConnectorId, CloudResourceType cloudResourceType) {
        CloudConnector cloudConnector = cloudConnectorRepositoryPort
                .findByClientId(cloudConnectorId)
                .orElseThrow();
        cloudConnector.addResourceType(cloudResourceType);
        cloudConnectorRepositoryPort.save(cloudConnector);
    }
}
