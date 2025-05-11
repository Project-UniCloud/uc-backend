package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloudmanagment.domain.CloudAccess;
import com.unicloudapp.cloudmanagment.domain.CloudAccessClientController;
import com.unicloudapp.cloudmanagment.domain.CloudAccessId;
import com.unicloudapp.cloudmanagment.domain.ExternalUserId;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    public CloudAccess giveCloudAccess(UserId userId,
                                       CloudAccessClientId cloudAccessClientId
    ) {
        try {
            AdapterInterface.CreateUserRequest request = AdapterInterface.CreateUserRequest.newBuilder()
                    .build();
            AdapterInterface.UserCreatedResponse response = stub.createUser(request);
            return CloudAccess.builder()
                    .cloudAccessId(CloudAccessId.of(UUID.randomUUID()))
                    .cloudAccessClientId(cloudAccessClientId)
                    .externalUserId(ExternalUserId.of(response.getId()))
                    .userId(userId)
                    .build();
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
}
