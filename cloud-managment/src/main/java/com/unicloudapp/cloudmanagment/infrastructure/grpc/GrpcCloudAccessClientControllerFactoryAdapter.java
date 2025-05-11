package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloudmanagment.application.CloudAccessClientControllerFactoryPort;
import com.unicloudapp.cloudmanagment.domain.CloudAccessClientController;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
class GrpcCloudAccessClientControllerFactoryAdapter implements CloudAccessClientControllerFactoryPort {

    @Override
    public CloudAccessClientController create(String host,
                                              int port
    ) {
        var channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        var stub = CloudAdapterGrpc.newBlockingStub(channel);
        return new GrpcCloudAccessClientController(channel, stub);
    }
}
