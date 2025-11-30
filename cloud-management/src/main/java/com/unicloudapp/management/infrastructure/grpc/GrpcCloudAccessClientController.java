package com.unicloudapp.management.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.management.domain.access_client.CloudResourceAccessClientController;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
class GrpcCloudResourceAccessClientController implements CloudResourceAccessClientController {

    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub;

    @Override
    public void createGroup(
            GroupUniqueName groupUniqueName,
            List<UserLogin> lecturerLogins,
            CloudResourceType resourceType
    ) {
        AdapterInterface.CreateGroupWithLeadersRequest request = AdapterInterface.CreateGroupWithLeadersRequest
                .newBuilder()
                .setResourceType(resourceType.getName())
                .setGroupName(groupUniqueName.toString())
                .addAllLeaders(lecturerLogins.stream().map(UserLogin::toString).toList())
                .build();
        AdapterInterface.GroupCreatedResponse response = stub.createGroupWithLeaders(request);
        GroupUniqueName.fromString(response.getGroupName());
    }

    @Override
    public boolean isRunning() {
        AdapterInterface.StatusRequest request = AdapterInterface.StatusRequest.newBuilder().build();
        AdapterInterface.StatusResponse response = stub.getStatus(request);
        return response.getIsHealthy();
    }

    @Override
    public boolean isCloudGroupExists(GroupUniqueName groupUniqueName) {
        AdapterInterface.GroupExistsRequest request = AdapterInterface.GroupExistsRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .build();
        AdapterInterface.GroupExistsResponse response = stub.groupExists(request);
        return response.getExists();
    }

    @Override
    public String createUsers(List<UserLogin> users, GroupUniqueName groupUniqueName) {
        AdapterInterface.CreateUsersForGroupRequest request = AdapterInterface.CreateUsersForGroupRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .addAllUsers(users.stream().map(UserLogin::getValue).toList())
                .build();
        return stub.createUsersForGroup(request).getMessage();
    }

    @Override
    public Map<GroupUniqueName, UsedLimit> updateUsedCost(LocalDate startDate, LocalDate endDate) {
        AdapterInterface.CostRequest request = AdapterInterface.CostRequest.newBuilder()
                .setStartDate(startDate.toString())
                .setEndDate(endDate.toString())
                .build();
        AdapterInterface.AllGroupsCostResponse totalCostsForAllGroups = stub.getTotalCostsForAllGroups(request);
        return totalCostsForAllGroups.getGroupCostsList()
                .stream()
                .collect(Collectors.toMap(
                        groupCost -> GroupUniqueName.fromString(groupCost.getGroupName()),
                        groupCost -> UsedLimit.of(BigDecimal.valueOf(groupCost.getAmount()))
                ));
    }

    @Override
    public void cleanUpResources(GroupUniqueName groupUniqueName, boolean force) {
        AdapterInterface.CleanupGroupRequest request = AdapterInterface.CleanupGroupRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .setForce(force)
                .build();
        AdapterInterface.CleanupGroupResponse response = stub.cleanupGroupResources(request);
        if (!response.getSuccess()) {
            throw new RuntimeException("Cleanup group resources failed. Message: " + response.getMessage());
        }
        log.info("Cleanup group resources successful for group: {}. Deleted resources: {}",
                groupUniqueName,
                String.join(", ", response.getDeletedResourcesList())
        );
    }

    @Override
    public void removeGroup(GroupUniqueName groupUniqueName) {
        AdapterInterface.RemoveGroupRequest request = AdapterInterface.RemoveGroupRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .build();
        AdapterInterface.RemoveGroupResponse response = stub.removeGroup(request);
        if (!response.getSuccess()) {
            throw new RuntimeException("Remove group failed. Message: " + response.getMessage());
        }
        log.info("Remove group successful for group: {}. Removed users: {}",
                groupUniqueName,
                String.join(", ", response.getRemovedUsersList())
        );
    }
}
