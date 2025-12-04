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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.support.CronExpression;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudRestControllerTest {

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
    @DisplayName("getCloudConnectors maps domain page to view page")
    void getCloudConnectors_mapsPage() {
        CloudConnector a = buildConnector(
                "a-client", "A", "localhost", 1234,
                new BigDecimal("10.00"), "0 0 * * * *",
                new ArrayList<>(List.of(CloudResourceType.of("S3")))
        );
        CloudConnector b = buildConnector(
                "b-client", "B", "localhost", 1235,
                new BigDecimal("1"), "0 */5 * * * *",
                new ArrayList<>(List.of(CloudResourceType.of("EC2")))
        );
        Page<CloudConnector> page = new PageImpl<>(List.of(a, b), PageRequest.of(0, 10), 2);
        when(cloudResourceAccessService.getCloudResourceAccessClients(PageRequest.of(0, 10))).thenReturn(page);

        Page<CloudConnectorRowView> result = controller.getCloudConnectors(0, 10);

        assertEquals(2, result.getContent().size());
        CloudConnectorRowView first = result.getContent().getFirst();
        assertEquals("a-client", first.cloudConnectorId());
        assertEquals("A", first.cloudConnectorName());
        assertEquals(new BigDecimal("10.00"), first.costLimit());
        assertEquals("0 0 * * * *", first.defaultCronExpression());
    }

    @Test
    @DisplayName("getCloudConnectorDetails returns details dto with inactive flag")
    void getCloudConnectorDetails_returnsDto() {
        CloudConnector details = buildConnector(
                "conn-1", "Connector 1", "h", 1,
                new BigDecimal("99.99"), "0 */10 * * * *",
                new ArrayList<>()
        );
        when(cloudResourceAccessService.getCloudResourceAccessClientDetails(CloudConnectorId.of("conn-1")))
                .thenReturn(details);

        CloudConnectorDetailsDto dto = controller.getCloudConnectorDetails("conn-1");

        assertEquals("conn-1", dto.cloudConnectorId());
        assertEquals("Connector 1", dto.cloudConnectorName());
        assertEquals(new BigDecimal("99.99"), dto.costLimit());
        assertEquals("0 */10 * * * *", dto.defaultCronExpression());
        assertFalse(dto.isActive());
    }

    @Test
    @DisplayName("postCloudConnector delegates to service with converted types")
    void postCloudConnector_delegates() {
        CloudConnectorSaveRequestDto request = new CloudConnectorSaveRequestDto(
                "conn-2", "localhost", 8080,
                new BigDecimal("12.34"), "0 */15 * * * *", "My Connector"
        );

        controller.postCloudConnector(request);

        ArgumentCaptor<CloudConnectorId> idCaptor = ArgumentCaptor.forClass(CloudConnectorId.class);
        ArgumentCaptor<String> hostCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> portCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<CostLimit> limitCaptor = ArgumentCaptor.forClass(CostLimit.class);
        ArgumentCaptor<CronExpression> cronCaptor = ArgumentCaptor.forClass(CronExpression.class);
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);

        verify(cloudConnectorService).createConnector(
                idCaptor.capture(), hostCaptor.capture(), portCaptor.capture(),
                limitCaptor.capture(), cronCaptor.capture(), nameCaptor.capture()
        );

        assertEquals("conn-2", idCaptor.getValue().id());
        assertEquals("localhost", hostCaptor.getValue());
        assertEquals(8080, portCaptor.getValue());
        assertEquals(new BigDecimal("12.34"), limitCaptor.getValue().getCost());
        assertEquals("0 */15 * * * *", cronCaptor.getValue().toString());
        assertEquals("My Connector", nameCaptor.getValue());
    }

    @Test
    @DisplayName("patchCloudConnector maps string types to value objects and delegates")
    void patchCloudConnector_delegates() {
        CloudConnectorResourceTypeRequestDto request = new CloudConnectorResourceTypeRequestDto("S3");
        when(cloudResourceAccessService.getCloudResourceAccessClientDetails(any()))
                .thenReturn(buildConnector("conn-3", "Connector 3", "localhost", 1234, new BigDecimal("10.00"), "0 */10 * * * *", new ArrayList<>()));

        controller.addCloudConnectorResourceType("conn-3", request);

        ArgumentCaptor<CloudConnectorId> idCaptor = ArgumentCaptor.forClass(CloudConnectorId.class);
        ArgumentCaptor<CloudResourceType> typeCaptor = ArgumentCaptor.forClass(CloudResourceType.class);

        verify(cloudConnectorService).addResourceType(idCaptor.capture(), typeCaptor.capture());
        assertEquals("conn-3", idCaptor.getValue().id());
        assertEquals(CloudResourceType.of("S3"), typeCaptor.getValue());
    }

    @Test
    @DisplayName("getCloudResourceTypesForCloudResourceAccessClient delegates to service")
    void getResourceTypes_returnsList() {
        when(cloudResourceAccessService.getCloudResourceTypesForCloudResourceAccessClient(CloudConnectorId.of("conn-4")))
                .thenReturn(List.of(CloudResourceType.of("S3"), CloudResourceType.of("EC2")));

        List<CloudResourceType> res = controller.getCloudResourceTypesForCloudResourceAccessClient(CloudConnectorId.of("conn-4"));
        assertEquals(2, res.size());
        assertEquals(CloudResourceType.of("S3"), res.get(0));
        assertEquals(CloudResourceType.of("EC2"), res.get(1));
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