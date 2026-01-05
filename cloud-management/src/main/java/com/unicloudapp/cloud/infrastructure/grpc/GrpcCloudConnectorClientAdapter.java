package com.unicloudapp.cloud.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloud.application.port.CloudConnectorClientPort;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.UsedLimit;
import com.unicloudapp.common.vo.user.UserLogin;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
class GrpcCloudConnectorClientAdapter implements CloudConnectorClientPort {

    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub;

    @Override
    public void createGroup(
            GroupUniqueName groupUniqueName, List<UserLogin> lecturerLogins, CloudResourceType resourceType) {
        AdapterInterface.CreateGroupWithLeadersRequest request =
                AdapterInterface.CreateGroupWithLeadersRequest.newBuilder()
                        .addAllResourceTypes(List.of(resourceType.getName()))
                        .setGroupName(groupUniqueName.toString())
                        .addAllLeaders(
                                lecturerLogins.stream().map(UserLogin::toString).toList())
                        .build();
        AdapterInterface.GroupCreatedResponse response = stub.createGroupWithLeaders(request);
        GroupUniqueName.fromString(response.getGroupName());
    }

    @Override
    public boolean isRunning() {
        AdapterInterface.StatusRequest request =
                AdapterInterface.StatusRequest.newBuilder().build();
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
    public String removeUser(UserLogin user, GroupUniqueName groupUniqueName) {
        AdapterInterface.DeleteUserRequest request = AdapterInterface.DeleteUserRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .setUserName(user.getValue())
                .build();
        return stub.deleteUser(request).getMessage();
    }

    @Override
    public UsedLimit updateUsedCost(LocalDate startDate, LocalDate endDate, GroupUniqueName groupUniqueName) {
        AdapterInterface.CostRequest request = AdapterInterface.CostRequest.newBuilder()
                .setStartDate(startDate.toString())
                .setEndDate(endDate.toString())
                .setGroupName(groupUniqueName.toString())
                .build();
        AdapterInterface.CostResponse totalCostsForAllGroups = stub.getTotalCostForGroup(request);
        return UsedLimit.of(BigDecimal.valueOf(totalCostsForAllGroups.getAmount()));
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
        log.info(
                "Cleanup group resources successful for group: {}. Deleted resources: {}",
                groupUniqueName,
                String.join(", ", response.getDeletedResourcesList()));
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
        log.info(
                "Remove group successful for group: {}. Removed users: {}",
                groupUniqueName,
                String.join(", ", response.getRemovedUsersList()));
    }

    @Override
    public Integer countCloudResources(GroupUniqueName groupUniqueName, CloudResourceType resourceType) {
        AdapterInterface.ResourceCountRequest request = AdapterInterface.ResourceCountRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .setResourceType(resourceType.getName())
                .build();
        AdapterInterface.ResourceCountResponse response = stub.getResourceCount(request);
        return response.getCount();
    }

    @Override
    public Map<CloudResourceType, BigDecimal> getCostsPerResourceType(GroupUniqueName groupUniqueName) {
        Map<CloudResourceType, BigDecimal> costsPerResourceType = new HashMap<>();
        AdapterInterface.GroupLast6MonthsCostRequest request = AdapterInterface.GroupLast6MonthsCostRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .build();
        AdapterInterface.GroupCostMapResponse response = stub.getGroupCostsLast6MonthsByService(request);
        response.getCostsMap()
                .forEach(
                        (key, value) -> costsPerResourceType.put(CloudResourceType.of(key), BigDecimal.valueOf(value)));
        return costsPerResourceType;
    }

    @Override
    public Map<LocalDate, BigDecimal> getTotalCostInTime(GroupUniqueName groupUniqueName) {
        Map<LocalDate, BigDecimal> costsInTime = new TreeMap<>();
        AdapterInterface.GroupLast6MonthsCostRequest request = AdapterInterface.GroupLast6MonthsCostRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .build();
        AdapterInterface.GroupMonthlyCostsResponse response = stub.getGroupMonthlyCostsLast6Months(request);
        response.getMonthCostsMap()
                .forEach((key, value) -> costsInTime.put(
                        LocalDate.parse(key, DateTimeFormatter.ofPattern("dd-MM-yyyy")), BigDecimal.valueOf(value)));
        return costsInTime;
    }

    @Override
    public List<CloudResourceType> getSupportedResourceTypes() {
        AdapterInterface.GetAvailableServicesRequest request =
                AdapterInterface.GetAvailableServicesRequest.newBuilder().build();
        try {
            AdapterInterface.GetAvailableServicesResponse response = stub.getAvailableServices(request);
            return response.getServicesList().stream()
                    .map(CloudResourceType::of)
                    .toList();
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNIMPLEMENTED) {
                log.warn(
                        "Cloud adapter does not implement GetAvailableServices yet. Falling back to empty supported types list.");
                return List.of();
            }
            throw e;
        }
    }

    @Override
    public void assignCloudResourceAccess(
            CloudResourceType resourceType, GroupUniqueName groupUniqueName, UserLogin lecturer) {
        AdapterInterface.AssignPoliciesRequest.Builder builder = AdapterInterface.AssignPoliciesRequest.newBuilder()
                .addAllResourceTypes(List.of(resourceType.getName()));
        if (groupUniqueName != null) {
            builder.setGroupName(groupUniqueName.toString());
        }
        if (lecturer != null) {
            builder.setUserName(lecturer.toString());
        }
        try {
            AdapterInterface.AssignPoliciesResponse response = stub.assignPolicies(builder.build());
            if (!response.getSuccess()) {
                throw new RuntimeException("Assign policies failed. Message: " + response.getMessage());
            }
            log.info(
                    "Policies assigned successfully for group: {}. Lecturer: {}. {}",
                    groupUniqueName,
                    lecturer,
                    response.getMessage());
        } catch (StatusRuntimeException e) {
            log.warn(e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Map.Entry<Boolean, String> addLecturerForGroup(GroupUniqueName groupUniqueName, UserLogin lecturerLogin) {
        var request = AdapterInterface.AddLeaderToGroupRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .setLeaderName(lecturerLogin.getValue())
                .build();
        var response = stub.addLeaderToGroup(request);
        return Map.entry(response.getSuccess(), response.getMessage());
    }
}
