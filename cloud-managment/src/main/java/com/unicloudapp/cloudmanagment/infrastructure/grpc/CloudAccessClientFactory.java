package com.unicloudapp.cloudmanagment.infrastructure.grpc;

import com.unicloudapp.cloudmanagment.application.CloudAccessClientPort;
import com.unicloudapp.cloudmanagment.domain.*;
import com.unicloudapp.users.application.UserService;
import org.springframework.scheduling.support.CronExpression;

import java.util.HashMap;
import java.util.Map;

class CloudAccessClientFactory {

    Map<String, CloudAccessClientPort> createCloudAccessClients(CloudAccessProperties properties, UserService userService) {
        Map<String, CloudAccessClientPort> clients = new HashMap<>();
        for (Map.Entry<String, CloudAccessProperties.SingleCloudAccessProperties> entry : properties.clients().entrySet()) {
            String cloudAccessId = entry.getKey();
            CloudAccessProperties.SingleCloudAccessProperties config = entry.getValue();
            CloudAccess cloudAccess = CloudAccess.builder()
                    .cloudAccessId(CloudAccessId.of(cloudAccessId))
                    .host(CloudAccessHost.of(config.host()))
                    .port(CloudAccessPort.of(config.port()))
                    .costLimit(CostLimit.of(config.costLimit()))
                    .cronExpression(CronExpression.parse(config.cronExpression()))
                    .build();
            CloudAccessClientAdapter client = new CloudAccessClientAdapter(cloudAccess, userService);
            clients.put(cloudAccessId, client);
        }
        return clients;
    }
}
