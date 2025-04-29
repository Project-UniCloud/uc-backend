package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloudmanagment.application.CloudAccessClientPort;
import com.unicloudapp.cloudmanagment.domain.CloudAccess;
import com.unicloudapp.users.application.UserDTO;
import com.unicloudapp.users.application.UserService;
import com.unicloudapp.users.domain.User;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

class CloudAccessClientAdapter implements CloudAccessClientPort {

    private final ManagedChannel channel;
    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub;
    private final UserService userService;

    CloudAccessClientAdapter(CloudAccess cloudAccess, UserService userService) {
        this.channel = ManagedChannelBuilder
                .forAddress(cloudAccess.getHost().getHost(), cloudAccess.getPort().getPort())
                .usePlaintext()
                .build();
        this.stub = CloudAdapterGrpc.newBlockingStub(channel);
        this.userService = userService;
    }

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
    public User createUser(UserDTO userDTO) {
        return userService.createUser(userDTO);
    }
}
