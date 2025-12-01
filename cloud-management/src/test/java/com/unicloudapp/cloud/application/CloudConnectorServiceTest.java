package com.unicloudapp.cloud.application;

import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.scheduling.support.CronExpression;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudConnectorServiceTest {

    CloudConnectorRepositoryPort repository;
    CloudConnectorService service;

    @BeforeEach
    void setUp() {
        repository = mock(CloudConnectorRepositoryPort.class);
        service = new CloudConnectorService(repository);
    }

    @Test
    @DisplayName("createConnector saves new connector with provided fields; errors on duplicate id")
    void createConnector_behavior() {
        CloudConnectorId id = CloudConnectorId.of("conn-1");
        String host = "localhost";
        int port = 8080;
        CostLimit limit = CostLimit.of(new BigDecimal("12.34"));
        CronExpression cron = CronExpression.parse("0 */10 * * * *");
        String name = "My Connector";

        // Not present -> should save
        when(repository.findByClientId(id)).thenReturn(Optional.empty());

        service.createConnector(id, host, port, limit, cron, name);

        ArgumentCaptor<CloudConnector> captor = ArgumentCaptor.forClass(CloudConnector.class);
        verify(repository).save(captor.capture());
        CloudConnector saved = captor.getValue();
        assertEquals(id, saved.getCloudConnectorId());
        assertEquals(host, saved.getHost());
        assertEquals(port, saved.getPort());
        assertEquals(limit, saved.getDefaultCostLimit());
        assertEquals(cron, saved.getCronExpression());
        assertEquals(name, saved.getName());
        assertTrue(saved.getResourceTypes().isEmpty());

        // Present -> should throw and not save
        reset(repository);
        when(repository.findByClientId(id)).thenReturn(Optional.of(saved));
        assertThrows(IllegalArgumentException.class, () ->
                service.createConnector(id, host, port, limit, cron, name)
        );
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("setResourceType adds new types, avoids duplicates, and saves; throws when id not found")
    void setResourceType_behavior() {
        CloudConnectorId id = CloudConnectorId.of("conn-2");
        CloudResourceType s3 = CloudResourceType.of("S3");
        CloudResourceType ec2 = CloudResourceType.of("EC2");

        // Existing connector with one type already present
        CloudConnector existing = CloudConnector.builder()
                .cloudConnectorId(id)
                .host("h")
                .port(1)
                .defaultCostLimit(CostLimit.of(BigDecimal.ZERO))
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .name("N")
                .resourceTypes(new ArrayList<>(List.of(s3)))
                .build();

        when(repository.findByClientId(id)).thenReturn(Optional.of(existing));

        // Add duplicate S3 and new EC2
        service.setResourceType(id, List.of(s3, ec2));

        ArgumentCaptor<CloudConnector> savedCaptor = ArgumentCaptor.forClass(CloudConnector.class);
        verify(repository).save(savedCaptor.capture());
        CloudConnector saved = savedCaptor.getValue();
        assertTrue(saved.containsResourceType(s3));
        assertTrue(saved.containsResourceType(ec2));
        assertEquals(2, saved.getResourceTypes().size());

        // Not found path -> throws NoSuchElementException
        CloudConnectorId missing = CloudConnectorId.of("missing");
        when(repository.findByClientId(missing)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.setResourceType(missing, List.of(s3)));
    }
}
