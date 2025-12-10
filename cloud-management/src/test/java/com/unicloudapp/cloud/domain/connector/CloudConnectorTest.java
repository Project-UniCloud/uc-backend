package com.unicloudapp.cloud.domain.connector;

import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.CronExpression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CloudConnectorTest {

    private CloudConnector buildSample(List<CloudResourceType> resourceTypes) {
        return CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of("connector-1"))
                .host("localhost")
                .port(8080)
                .defaultCostLimit(CostLimit.of(new BigDecimal("10.00")))
                .cronExpression(CronExpression.parse("0 0 * * * *"))
                .name("Test Connector")
                .resourceTypes(resourceTypes)
                .build();
    }

    @Test
    @DisplayName("builder populates fields and getters return values")
    void builder_and_getters() {
        List<CloudResourceType> types = new ArrayList<>(List.of(CloudResourceType.of("S3")));
        CloudConnector connector = buildSample(types);

        assertEquals("connector-1", connector.getCloudConnectorId().id());
        assertEquals("localhost", connector.getHost());
        assertEquals(8080, connector.getPort());
        assertEquals(new BigDecimal("10.00"), connector.getDefaultCostLimit().getCost());
        assertEquals("0 0 * * * *", connector.getCronExpression().toString());
        assertEquals("Test Connector", connector.getName());
        assertTrue(connector.getResourceTypes().contains(CloudResourceType.of("S3")));
    }

    @Test
    @DisplayName("withers create new instances with updated fields, leaving original intact")
    void withers_update_fields_immutably() {
        List<CloudResourceType> types = new ArrayList<>(List.of(CloudResourceType.of("S3")));
        CloudConnector original = buildSample(types);

        CloudConnector withName = original.withName("Renamed");
        CloudConnector withCron = original.withCronExpression(CronExpression.parse("0 */5 * * * *"));
        CloudConnector withLimit = original.withDefaultCostLimit(CostLimit.of(new BigDecimal("99.99")));

        // Original unchanged
        assertEquals("Test Connector", original.getName());
        assertEquals("0 0 * * * *", original.getCronExpression().toString());
        assertEquals(new BigDecimal("10.00"), original.getDefaultCostLimit().getCost());

        // New instances updated
        assertEquals("Renamed", withName.getName());
        assertEquals("0 */5 * * * *", withCron.getCronExpression().toString());
        assertEquals(new BigDecimal("99.99"), withLimit.getDefaultCostLimit().getCost());

        // Other fields preserved
        assertEquals(original.getCloudConnectorId(), withName.getCloudConnectorId());
        assertEquals(original.getHost(), withCron.getHost());
        assertEquals(original.getPort(), withLimit.getPort());
        assertEquals(original.getResourceTypes(), withName.getResourceTypes());
    }
}
