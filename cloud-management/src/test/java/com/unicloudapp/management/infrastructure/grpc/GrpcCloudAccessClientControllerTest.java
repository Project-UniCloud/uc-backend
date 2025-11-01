package com.unicloudapp.management.infrastructure.grpc;

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

class GrpcCloudAccessClientControllerTest {

    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub = mock(CloudAdapterGrpc.CloudAdapterBlockingStub.class);
    private final GrpcCloudAccessClientController controller = new GrpcCloudAccessClientController(stub);

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
        assertEquals("S3", sent.getResourceType());
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
    @DisplayName("updateUsedCost maps group costs to map of GroupUniqueName -> UsedLimit")
    void updateUsedCost_maps() {
        AdapterInterface.GroupCost gc1 = AdapterInterface.GroupCost.newBuilder()
                .setGroupName("AI 2024L")
                .setAmount(12.34)
                .build();
        AdapterInterface.GroupCost gc2 = AdapterInterface.GroupCost.newBuilder()
                .setGroupName("DS 2025Z")
                .setAmount(0.0)
                .build();
        AdapterInterface.AllGroupsCostResponse resp = AdapterInterface.AllGroupsCostResponse.newBuilder()
                .addGroupCosts(gc1)
                .addGroupCosts(gc2)
                .build();
        when(stub.getTotalCostsForAllGroups(any())).thenReturn(resp);

        Map<GroupUniqueName, UsedLimit> map = controller.updateUsedCost(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31));
        assertEquals(2, map.size());
        assertEquals(0, map.get(GroupUniqueName.fromString("DS 2025Z")).getValue().compareTo(BigDecimal.ZERO));
        assertEquals(0, map.get(GroupUniqueName.fromString("AI 2024L")).getValue().compareTo(new BigDecimal("12.34")));
    }

    @Test
    @DisplayName("updateUsedCost with no costs returns empty map")
    void updateUsedCost_empty() {
        AdapterInterface.AllGroupsCostResponse resp = AdapterInterface.AllGroupsCostResponse.newBuilder().build();
        when(stub.getTotalCostsForAllGroups(any())).thenReturn(resp);
        Map<GroupUniqueName, UsedLimit> map = controller.updateUsedCost(LocalDate.EPOCH, LocalDate.EPOCH.plusDays(1));
        assertTrue(map.isEmpty());
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
}
