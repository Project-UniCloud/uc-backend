package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.cloudmanagment.domain.CloudAccessClient;
import com.unicloudapp.cloudmanagment.domain.CloudResourceAccessFactory;
import com.unicloudapp.common.domain.cloud.CloudResourceType;
import com.unicloudapp.cloudmanagment.domain.CostLimit;
import com.unicloudapp.common.domain.cloud.CloudAccessClientId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
class CloudAccessServiceConfig {

    @Bean
    CloudAccessService cloudAccessService(
            CloudResourceAccessRepositoryPort repository,
            CloudAccessClientProperties cloudAccessClientProperties,
            CloudAccessClientControllerFactoryPort cloudAccessClientControllerFactory
    ) {
        var clients = cloudAccessClientProperties.clients()
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
                                .defaultCostLimit(CostLimit.of(entry.getValue().costLimit()))
                                .name(entry.getValue().name())
                                .cloudResourceAccessFactory(new CloudResourceAccessFactory())
                                .resourceTypes(entry.getValue()
                                        .resourceTypes()
                                        .stream()
                                        .map(CloudResourceType::of)
                                        .toList()
                                ).build()
                        )
                );
        return new CloudAccessService(clients, repository);
    }
}
