package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloudmanagment.domain.CloudAccessClientController;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
class GrpcCloudAccessClientController implements CloudAccessClientController {

    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub;

    @Override
    public GroupUniqueName createGroup(
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
        return GroupUniqueName.fromString(response.getGroupName());
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
    public String createUsers(List<String> users, GroupUniqueName groupUniqueName) {
        AdapterInterface.CreateUsersForGroupRequest request = AdapterInterface.CreateUsersForGroupRequest.newBuilder()
                .setGroupName(groupUniqueName.toString())
                .addAllUsers(users)
                .build();
        return stub.createUsersForGroup(request).getMessage();
    }
}
