package com.unicloudapp.cloud.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GrpcCloudConnectorControllerTest {

    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub = mock(CloudAdapterGrpc.CloudAdapterBlockingStub.class);
    private final GrpcCloudConnectorClientAdapter controller = new GrpcCloudConnectorClientAdapter(stub);

    @Test
    @DisplayName("createGroup sends proper request and validates response group name")
    void createGroup_buildsRequest_andParsesResponse() {
        // Arrange
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        List<UserLogin> leaders = List.of(UserLogin.of("leader1"), UserLogin.of("leader2"));
        CloudResourceType type = CloudResourceType.of("S3");

        AdapterInterface.GroupCreatedResponse response = AdapterInterface.GroupCreatedResponse.newBuilder()
                .setGroupName("AI 2024L")
                .build();
        ArgumentCaptor<AdapterInterface.CreateGroupWithLeadersRequest> captor = ArgumentCaptor.forClass(AdapterInterface.CreateGroupWithLeadersRequest.class);
        when(stub.createGroupWithLeaders(captor.capture())).thenReturn(response);

        // Act
        controller.createGroup(group, leaders, type);

        // Assert
        AdapterInterface.CreateGroupWithLeadersRequest sent = captor.getValue();
        assertEquals("S3", sent.getResourceTypesList().getFirst());
        assertEquals("AI 2024L", sent.getGroupName());
        assertEquals(List.of("leader1", "leader2"), sent.getLeadersList());
        // and no exception thrown by parsing the response group name
    }

    @Test
    @DisplayName("isRunning delegates to stub.getStatus")
    void isRunning_delegates() {
        AdapterInterface.StatusResponse resp = AdapterInterface.StatusResponse.newBuilder()
                .setIsHealthy(true)
                .build();
        when(stub.getStatus(any())).thenReturn(resp);

        boolean healthy = controller.isRunning();
        assertTrue(healthy);
        verify(stub).getStatus(AdapterInterface.StatusRequest.newBuilder().build());
    }

    @Test
    @DisplayName("isCloudGroupExists returns exists flag")
    void isCloudGroupExists_returnsFlag() {
        GroupUniqueName name = GroupUniqueName.fromString("AI 2024L");
        AdapterInterface.GroupExistsResponse resp = AdapterInterface.GroupExistsResponse.newBuilder()
                .setExists(true)
                .build();
        ArgumentCaptor<AdapterInterface.GroupExistsRequest> captor = ArgumentCaptor.forClass(AdapterInterface.GroupExistsRequest.class);
        when(stub.groupExists(captor.capture())).thenReturn(resp);

        boolean exists = controller.isCloudGroupExists(name);
        assertTrue(exists);
        assertEquals("AI 2024L", captor.getValue().getGroupName());
    }

    @Test
    @DisplayName("createUsers returns message and sends proper request")
    void createUsers_returnsMessage() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        List<UserLogin> users = List.of(UserLogin.of("u1"), UserLogin.of("u2"));
        AdapterInterface.CreateUsersForGroupResponse resp = AdapterInterface.CreateUsersForGroupResponse.newBuilder()
                .setMessage("ok")
                .build();
        ArgumentCaptor<AdapterInterface.CreateUsersForGroupRequest> captor = ArgumentCaptor.forClass(AdapterInterface.CreateUsersForGroupRequest.class);
        when(stub.createUsersForGroup(captor.capture())).thenReturn(resp);

        String message = controller.createUsers(users, group);
        assertEquals("ok", message);
        AdapterInterface.CreateUsersForGroupRequest sent = captor.getValue();
        assertEquals("AI 2024L", sent.getGroupName());
        assertEquals(List.of("u1", "u2"), sent.getUsersList());
    }

    @Test
    @DisplayName("updateUsedCost returns UsedLimit for specified group")
    void updateUsedCost_returnsUsedLimit() {
        AdapterInterface.CostResponse resp = AdapterInterface.CostResponse.newBuilder()
                .setAmount(12.34)
                .build();
        when(stub.getTotalCostForGroup(any())).thenReturn(resp);

        UsedLimit usedLimit = controller.updateUsedCost(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), GroupUniqueName.fromString("AI 2024L"));
        assertEquals(0, usedLimit.getValue().compareTo(new BigDecimal("12.34")));
    }

    @Test
    @DisplayName("updateUsedCost with no costs returns zero UsedLimit")
    void updateUsedCost_empty() {
        AdapterInterface.CostResponse resp = AdapterInterface.CostResponse.newBuilder().setAmount(0.0).build();
        when(stub.getTotalCostForGroup(any())).thenReturn(resp);
        UsedLimit usedLimit = controller.updateUsedCost(LocalDate.EPOCH, LocalDate.EPOCH.plusDays(1), GroupUniqueName.fromString("AI 2024L"));
        assertEquals(0, usedLimit.getValue().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("cleanUpResources success does not throw and sends request with force=true")
    void cleanUpResources_success() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        AdapterInterface.CleanupGroupResponse resp = AdapterInterface.CleanupGroupResponse.newBuilder()
                .setSuccess(true)
                .addDeletedResources("bucket1")
                .build();
        ArgumentCaptor<AdapterInterface.CleanupGroupRequest> captor = ArgumentCaptor.forClass(AdapterInterface.CleanupGroupRequest.class);
        when(stub.cleanupGroupResources(captor.capture())).thenReturn(resp);

        assertDoesNotThrow(() -> controller.cleanUpResources(group, true));
        AdapterInterface.CleanupGroupRequest sent = captor.getValue();
        assertEquals("AI 2024L", sent.getGroupName());
        assertTrue(sent.getForce());
    }

    @Test
    @DisplayName("cleanUpResources failure throws RuntimeException with message")
    void cleanUpResources_failure() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        AdapterInterface.CleanupGroupResponse resp = AdapterInterface.CleanupGroupResponse.newBuilder()
                .setSuccess(false)
                .setMessage("oops")
                .build();
        when(stub.cleanupGroupResources(any())).thenReturn(resp);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.cleanUpResources(group, true));
        assertTrue(ex.getMessage().contains("Cleanup group resources failed"));
        assertTrue(ex.getMessage().contains("oops"));
    }
    
    @Test
    @DisplayName("removeGroup success sends request and does not throw")
    void removeGroup_success() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        AdapterInterface.RemoveGroupResponse resp = AdapterInterface.RemoveGroupResponse.newBuilder()
                .setSuccess(true)
                .addRemovedUsers("u1")
                .build();
        ArgumentCaptor<AdapterInterface.RemoveGroupRequest> captor = ArgumentCaptor.forClass(AdapterInterface.RemoveGroupRequest.class);
        when(stub.removeGroup(captor.capture())).thenReturn(resp);

        assertDoesNotThrow(() -> controller.removeGroup(group));
        AdapterInterface.RemoveGroupRequest sent = captor.getValue();
        assertEquals("AI 2024L", sent.getGroupName());
    }

    @Test
    @DisplayName("removeGroup failure throws RuntimeException with message")
    void removeGroup_failure() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        AdapterInterface.RemoveGroupResponse resp = AdapterInterface.RemoveGroupResponse.newBuilder()
                .setSuccess(false)
                .setMessage("oops")
                .build();
        when(stub.removeGroup(any())).thenReturn(resp);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> controller.removeGroup(group));
        assertTrue(ex.getMessage().contains("Remove group failed"));
        assertTrue(ex.getMessage().contains("oops"));
    }

    // ================= New tests for issue: countCloudResources, getCostsPerResourceType, getTotalCostInTime =================

    @Test
    @DisplayName("countCloudResources builds request and returns count")
    void countCloudResources_buildsRequest_andReturnsCount() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        CloudResourceType type = CloudResourceType.of("S3");

        AdapterInterface.ResourceCountResponse resp = AdapterInterface.ResourceCountResponse.newBuilder()
                .setCount(7)
                .build();
        ArgumentCaptor<AdapterInterface.ResourceCountRequest> captor = ArgumentCaptor.forClass(AdapterInterface.ResourceCountRequest.class);
        when(stub.getResourceCount(captor.capture())).thenReturn(resp);

        Integer count = controller.countCloudResources(group, type);

        assertEquals(7, count);
        AdapterInterface.ResourceCountRequest sent = captor.getValue();
        assertEquals("AI 2024L", sent.getGroupName());
        assertEquals("S3", sent.getResourceType());
    }

    @Test
    @DisplayName("countCloudResources returns zero when stub returns 0")
    void countCloudResources_zeroCount() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        CloudResourceType type = CloudResourceType.of("EC2");
        AdapterInterface.ResourceCountResponse resp = AdapterInterface.ResourceCountResponse.newBuilder()
                .setCount(0)
                .build();
        when(stub.getResourceCount(any())).thenReturn(resp);

        Integer count = controller.countCloudResources(group, type);
        assertEquals(0, count);
    }

    @Test
    @DisplayName("getCostsPerResourceType maps response to domain types and BigDecimal values")
    void getCostsPerResourceType_mapsResponseToDomain() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");

        AdapterInterface.GroupCostMapResponse resp = AdapterInterface.GroupCostMapResponse.newBuilder()
                .putCosts("S3", 12.34)
                .putCosts("EC2", 0.0)
                .build();
        ArgumentCaptor<AdapterInterface.GroupLast6MonthsCostRequest> captor = ArgumentCaptor.forClass(AdapterInterface.GroupLast6MonthsCostRequest.class);
        when(stub.getGroupCostsLast6MonthsByService(captor.capture())).thenReturn(resp);

        Map<com.unicloudapp.common.vo.cloud.CloudResourceType, java.math.BigDecimal> map = controller.getCostsPerResourceType(group);

        assertEquals(2, map.size());
        assertEquals(new java.math.BigDecimal("12.34"), map.get(com.unicloudapp.common.vo.cloud.CloudResourceType.of("S3")));
        assertEquals(new java.math.BigDecimal("0.0"), map.get(com.unicloudapp.common.vo.cloud.CloudResourceType.of("EC2")));
        assertEquals("AI 2024L", captor.getValue().getGroupName());
    }

    @Test
    @DisplayName("getCostsPerResourceType returns empty map when response has no entries")
    void getCostsPerResourceType_emptyMapReturnsEmpty() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        AdapterInterface.GroupCostMapResponse resp = AdapterInterface.GroupCostMapResponse.newBuilder().build();
        when(stub.getGroupCostsLast6MonthsByService(any())).thenReturn(resp);

        Map<com.unicloudapp.common.vo.cloud.CloudResourceType, java.math.BigDecimal> map = controller.getCostsPerResourceType(group);
        assertTrue(map.isEmpty());
    }

    @Test
    @DisplayName("getTotalCostInTime parses dd-MM-yyyy dates and returns chronologically ordered map")
    void getTotalCostInTime_parsesDatesAndMapsValues() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");

        AdapterInterface.GroupMonthlyCostsResponse resp = AdapterInterface.GroupMonthlyCostsResponse.newBuilder()
                .putMonthCosts("01-01-2024", 1.23)
                .putMonthCosts("15-02-2024", 4.56)
                .build();
        ArgumentCaptor<AdapterInterface.GroupLast6MonthsCostRequest> captor = ArgumentCaptor.forClass(AdapterInterface.GroupLast6MonthsCostRequest.class);
        when(stub.getGroupMonthlyCostsLast6Months(captor.capture())).thenReturn(resp);

        Map<java.time.LocalDate, java.math.BigDecimal> map = controller.getTotalCostInTime(group);

        java.time.LocalDate d1 = java.time.LocalDate.of(2024, 1, 1);
        java.time.LocalDate d2 = java.time.LocalDate.of(2024, 2, 15);
        assertEquals(new java.math.BigDecimal("1.23"), map.get(d1));
        assertEquals(new java.math.BigDecimal("4.56"), map.get(d2));
        assertEquals(java.util.List.of(d1, d2), map.keySet().stream().toList());
        assertEquals("AI 2024L", captor.getValue().getGroupName());
    }

    @Test
    @DisplayName("getTotalCostInTime returns empty map when response has no entries")
    void getTotalCostInTime_emptyMapReturnsEmpty() {
        GroupUniqueName group = GroupUniqueName.fromString("AI 2024L");
        AdapterInterface.GroupMonthlyCostsResponse resp = AdapterInterface.GroupMonthlyCostsResponse.newBuilder().build();
        when(stub.getGroupMonthlyCostsLast6Months(any())).thenReturn(resp);

        Map<java.time.LocalDate, java.math.BigDecimal> map = controller.getTotalCostInTime(group);
        assertTrue(map.isEmpty());
    }
}
