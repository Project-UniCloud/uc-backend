package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudAccess;
import com.unicloudapp.cloudmanagment.domain.CloudAccessClient;
import com.unicloudapp.cloudmanagment.domain.CostLimit;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import com.unicloudapp.common.domain.user.UserId;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CloudAccessService {

    private final Map<String, CloudAccessClient> clients;
    private final CloudAccessRepositoryPort cloudAccessRepository;

    public CloudAccessService(CloudAccessClientProperties cloudAccessClientProperties,
                              CloudAccessClientControllerFactoryPort cloudAccessClientControllerFactory,
                              CloudAccessRepositoryPort cloudAccessRepository
    ) {
        this.clients = cloudAccessClientProperties.clients()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> CloudAccessClient.builder()
                                .cloudAccessClientId(CloudAccessClientId.of(entry.getKey()))
                                .controller(cloudAccessClientControllerFactory.create(
                                        entry.getValue().host(),
                                        entry.getValue().port()
                                )).cronExpression(CronExpression.parse(entry.getValue().cronExpression()))
                                .costLimit(CostLimit.of(entry.getValue().costLimit()))
                                .build()
                        )
                );
        this.cloudAccessRepository = cloudAccessRepository;
    }

    public CloudAccess giveCloudAccess(UserId userId,
                                       CloudAccessClientId cloudAccessClientId
    ) {
        CloudAccessClient cloudAccessClient = clients.get(cloudAccessClientId.getValue());
        return cloudAccessRepository.save(
                cloudAccessClient.giveCloudAccess(userId, cloudAccessClientId)
        );
    }
}
