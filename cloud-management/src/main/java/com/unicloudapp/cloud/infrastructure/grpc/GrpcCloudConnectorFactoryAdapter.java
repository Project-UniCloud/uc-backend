package com.unicloudapp.cloud.infrastructure.grpc;

import adapter.CloudAdapterGrpc;
import com.unicloudapp.cloud.application.port.CloudConnectorClientFactoryPort;
import com.unicloudapp.cloud.application.port.CloudConnectorClientPort;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Component;

@Component
class GrpcCloudConnectorFactoryAdapter implements CloudConnectorClientFactoryPort {

    @Override
    public CloudConnectorClientPort create(String host, int port) {
        var channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        var stub = CloudAdapterGrpc.newBlockingStub(channel);
        return new GrpcCloudConnectorClientAdapter(stub);
    }
}
