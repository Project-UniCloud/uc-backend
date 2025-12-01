package com.unicloudapp.cloud.application;

import com.unicloudapp.cloud.application.port.CloudConnectorClientFactoryPort;
import com.unicloudapp.cloud.application.port.CloudConnectorRepositoryPort;
import com.unicloudapp.cloud.application.port.CloudResourceAccessRepositoryPort;
import com.unicloudapp.cloud.domain.access.CloudResourceAccessFactory;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.notifications.NotificationsCommandService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
class CloudResourceAccessServiceConfig {

    @Bean
    CloudResourceAccessService CloudResourceAccessService(
            CloudResourceAccessRepositoryPort repository,
            ThreadPoolTaskScheduler taskScheduler,
            GroupQueryService groupQueryService,
            NotificationsCommandService notificationsCommandService,
            CloudConnectorRepositoryPort cloudConnectorRepositoryPort,
            CloudConnectorClientFactoryPort cloudConnectorClientFactoryPort) {
        return new CloudResourceAccessService(
                taskScheduler,
                cloudConnectorRepositoryPort,
                repository,
                groupQueryService,
                notificationsCommandService,
                new CloudResourceAccessFactory(),
                cloudConnectorClientFactoryPort
        );
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("cloud-cleaner-");
        return scheduler;
    }
}
