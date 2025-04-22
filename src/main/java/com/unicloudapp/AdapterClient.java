package com.unicloudapp;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class AdapterClient {

    private final ManagedChannel channel;
    private final CloudAdapterGrpc.CloudAdapterBlockingStub stub;

    public AdapterClient(String host, int port) {
        this.channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        this.stub = CloudAdapterGrpc.newBlockingStub(channel);
    }

    public boolean getStatus() {
        AdapterInterface.StatusRequest request = AdapterInterface.StatusRequest.newBuilder()
                .build();
        AdapterInterface.StatusResponse response = stub.getStatus(request);
        return response.getIsHealthy();
    }

    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
