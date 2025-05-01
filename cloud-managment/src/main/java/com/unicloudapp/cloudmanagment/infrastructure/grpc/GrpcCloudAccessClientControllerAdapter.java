package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import com.unicloudapp.cloudmanagment.application.CloudAccessClientControllerFactoryPort;
import com.unicloudapp.cloudmanagment.domain.CloudAccessClientController;
import org.springframework.stereotype.Component;

@Component
class GrpcCloudAccessClientControllerAdapter implements CloudAccessClientControllerFactoryPort {

    @Override
    public CloudAccessClientController create(String host,
                                              int port
    ) {
        return new GrpcCloudAccessClientController(host, port);
    }
}
