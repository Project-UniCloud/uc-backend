package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloudmanagment.application.AdapterClientPort;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;

public class AdapterClientAdapter implements AdapterClientPort {

    private final ManagedChannel channel;
    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub;

    public AdapterClientAdapter(String host, int port) {
        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
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
    public boolean isServerRunning() {
        try {
            AdapterInterface.StatusRequest request = AdapterInterface.StatusRequest.newBuilder().build();
            AdapterInterface.StatusResponse response = stub.getStatus(request);
            return response.getIsHealthy();
        } catch (StatusRuntimeException e) {
            return false;
        }
    }
}
