package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloudmanagment.application.CloudAccessClientPort;
import com.unicloudapp.cloudmanagment.domain.CloudAccess;
import com.unicloudapp.users.application.UserService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

class CloudAccessClientAdapter implements CloudAccessClientPort {

    private final ManagedChannel channel;
    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub;

    CloudAccessClientAdapter(CloudAccess cloudAccess) {
        this.channel = ManagedChannelBuilder
                .forAddress(cloudAccess.getHost().getHost(), cloudAccess.getPort().getPort())
                .usePlaintext()
                .build();
        this.stub = CloudAdapterGrpc.newBlockingStub(channel);
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
}
