package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import com.unicloudapp.cloudmanagment.application.CloudAccessClientResolverPort;
import com.unicloudapp.user.application.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CloudAccessGrpcConfiguration {

    @Bean
    CloudAccessClientResolverPort cloudAccessClientPort(CloudAccessProperties properties, UserService userService) {
        var factory = new CloudAccessClientFactory();
        return new CloudAccessClientResolverAdapter(factory.createCloudAccessClients(properties, userService));
    }
}
