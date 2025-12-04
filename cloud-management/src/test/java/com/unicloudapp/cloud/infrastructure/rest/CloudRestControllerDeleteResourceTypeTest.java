package com.unicloudapp.cloud.infrastructure.rest;

import com.unicloudapp.cloud.application.CloudConnectorService;
import com.unicloudapp.cloud.application.CloudResourceAccessService;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudRestControllerDeleteResourceTypeTest {

    CloudResourceAccessService cloudResourceAccessService;
    CloudConnectorService cloudConnectorService;
    CloudRestController controller;

    @BeforeEach
    void setUp() {
        cloudResourceAccessService = mock(CloudResourceAccessService.class);
        cloudConnectorService = mock(CloudConnectorService.class);
        controller = new CloudRestController(cloudResourceAccessService, cloudConnectorService);
    }

    private CloudConnector buildConnector(String id,
                                          String name,
                                          String host,
                                          int port,
                                          BigDecimal defaultLimit,
                                          String cron,
                                          List<CloudResourceType> types) {
        return CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of(id))
                .name(name)
                .host(host)
                .port(port)
                .defaultCostLimit(CostLimit.of(defaultLimit))
                .cronExpression(CronExpression.parse(cron))
                .resourceTypes(types)
                .build();
    }

    @Test
    @DisplayName("deleteCloudConnectorResourceType removes the given type and delegates to service")
    void deleteCloudConnectorResourceType_removesTypeAndDelegates() {
        // Existing connector has S3 and EC2
        List<CloudResourceType> existing = new ArrayList<>(List.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2")));
        when(cloudResourceAccessService.getCloudResourceAccessClientDetails(CloudConnectorId.of("conn-5")))
                .thenReturn(buildConnector("conn-5", "Connector 5", "localhost", 1234, new BigDecimal("10.00"), "0 */10 * * * *", existing));

        CloudConnectorResourceTypeRequestDto request = new CloudConnectorResourceTypeRequestDto("EC2");

        controller.deleteCloudConnectorResourceType("conn-5", "EC2");

        ArgumentCaptor<CloudConnectorId> idCaptor = ArgumentCaptor.forClass(CloudConnectorId.class);
        ArgumentCaptor<CloudResourceType> typeCaptor = ArgumentCaptor.forClass(CloudResourceType.class);

        verify(cloudConnectorService).deleteResourceType(idCaptor.capture(), typeCaptor.capture());
        assertEquals("conn-5", idCaptor.getValue().id());
        assertEquals(CloudResourceType.of("EC2"), typeCaptor.getValue());
    }

    @Test
    @DisplayName("deleteCloudConnectorResourceType removing absent type keeps list unchanged and still delegates")
    void deleteCloudConnectorResourceType_removingAbsentTypeLeavesListUnchanged() {
        // Existing connector has only S3
        List<CloudResourceType> existing = new ArrayList<>(List.of(CloudResourceType.of("S3")));
        when(cloudResourceAccessService.getCloudResourceAccessClientDetails(CloudConnectorId.of("conn-6")))
                .thenReturn(buildConnector("conn-6", "Connector 6", "localhost", 1234, new BigDecimal("5.00"), "0 */10 * * * *", existing));

        CloudConnectorResourceTypeRequestDto request = new CloudConnectorResourceTypeRequestDto("EC2");

        controller.deleteCloudConnectorResourceType("conn-6", "EC2");

        ArgumentCaptor<CloudConnectorId> idCaptor = ArgumentCaptor.forClass(CloudConnectorId.class);
        ArgumentCaptor<CloudResourceType> typeCaptor = ArgumentCaptor.forClass(CloudResourceType.class);

        verify(cloudConnectorService).deleteResourceType(idCaptor.capture(), typeCaptor.capture());
        assertEquals("conn-6", idCaptor.getValue().id());
        assertEquals(CloudResourceType.of("EC2"), typeCaptor.getValue());
    }
}
