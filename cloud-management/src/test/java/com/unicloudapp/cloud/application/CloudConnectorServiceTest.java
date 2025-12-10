package com.unicloudapp.cloud.application;

import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CostLimit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.support.CronExpression;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CloudConnectorServiceTest {

    CloudConnectorRepositoryPort repository;
    ApplicationEventPublisher eventPublisher;
    CloudConnectorService service;

    @BeforeEach
    void setUp() {
        repository = mock(CloudConnectorRepositoryPort.class);
        eventPublisher = mock(ApplicationEventPublisher.class);
        service = new CloudConnectorService(eventPublisher, repository);
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
}
