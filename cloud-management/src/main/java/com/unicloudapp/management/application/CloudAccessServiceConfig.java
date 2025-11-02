package com.unicloudapp.management.application;

import com.unicloudapp.management.domain.CloudAccessClient;
import com.unicloudapp.management.domain.CloudResourceAccessFactory;
import com.unicloudapp.common.notifications.NotificationsCommandService;
import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CostLimit;
import com.unicloudapp.common.group.GroupQueryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronExpression;

import java.util.Map;
import java.util.stream.Collectors;

@Configuration
class CloudAccessServiceConfig {

    @Bean
    CloudAccessService cloudAccessService(
            CloudResourceAccessRepositoryPort repository,
            CloudAccessClientProperties cloudAccessClientProperties,
            CloudAccessClientControllerFactoryPort cloudAccessClientControllerFactory,
            ThreadPoolTaskScheduler taskScheduler,
            GroupQueryService groupQueryService,
            NotificationsCommandService notificationsCommandService
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
        return new CloudAccessService(taskScheduler, clients, repository, groupQueryService, notificationsCommandService);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("cloud-cleaner-");
        return scheduler;
    }
}
