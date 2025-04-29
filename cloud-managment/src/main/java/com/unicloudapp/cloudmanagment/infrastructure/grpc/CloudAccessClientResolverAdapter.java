package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import com.unicloudapp.cloudmanagment.application.CloudAccessClientPort;
import com.unicloudapp.cloudmanagment.application.CloudAccessClientResolverPort;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
class CloudAccessClientResolverAdapter implements CloudAccessClientResolverPort {

    private final Map<String, CloudAccessClientPort> cloudAccessClients;

    @Override
    public Optional<CloudAccessClientPort> getClient(String cloudAccessKey) {
        return Optional.ofNullable(cloudAccessClients.get(cloudAccessKey));
    }
}
