package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloudmanagment.domain.CloudAccessClientController;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.common.domain.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class GrpcCloudAccessClientController implements CloudAccessClientController {

    private final ManagedChannel channel;
    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub;

    @Override
    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
            try {
                if (!channel.awaitTermination(5, TimeUnit.SECONDS)) {
                    channel.shutdownNow();
                }
            } catch (InterruptedException e) {
                channel.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public GroupUniqueName createGroup(
            GroupUniqueName groupUniqueName,
            List<UserLogin> lecturerLogins,
            CloudResourceType resourceType
    ) {
        try {
            AdapterInterface.CreateGroupWithLeadersRequest request = AdapterInterface.CreateGroupWithLeadersRequest
                    .newBuilder()
                    .setResourceType(resourceType.getName())
                    .setGroupName(groupUniqueName.toString())
                    .addAllLeaders(lecturerLogins.stream().map(UserLogin::toString).collect(Collectors.toList()))
                    .build();
            AdapterInterface.GroupCreatedResponse response = stub.createGroupWithLeaders(request);
            return GroupUniqueName.fromString(response.getGroupName());
        } catch (StatusRuntimeException e) {
            return null;
        }
    }

    @Override
    public boolean isRunning() {
        try {
            AdapterInterface.StatusRequest request = AdapterInterface.StatusRequest.newBuilder().build();
            AdapterInterface.StatusResponse response = stub.getStatus(request);
            return response.getIsHealthy();
        } catch (StatusRuntimeException e) {
            return false;
        }
    }

    @Override
    public boolean isCloudGroupExists(GroupUniqueName groupUniqueName) {
        try {
            AdapterInterface.GroupExistsRequest request = AdapterInterface.GroupExistsRequest.newBuilder()
                    .setGroupName(groupUniqueName.toString())
                    .build();
            AdapterInterface.GroupExistsResponse response = stub.groupExists(request);
            return response.getExists();
        } catch (StatusRuntimeException e) {
            return false;
        }
    }
}
