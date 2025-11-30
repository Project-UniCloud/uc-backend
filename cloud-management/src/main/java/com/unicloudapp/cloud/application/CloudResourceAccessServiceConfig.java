package com.unicloudapp.cloud.application;

import com.unicloudapp.cloud.domain.access.CloudResourceAccessFactory;
import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.notifications.NotificationsCommandService;
import com.unicloudapp.cloud.application.port.CloudResourceAccessClientRepositoryPort;
import com.unicloudapp.cloud.application.port.CloudResourceAccessRepositoryPort;
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
            CloudResourceAccessClientRepositoryPort cloudResourceAccessClientRepositoryPort
    ) {
        return new CloudResourceAccessService(
                taskScheduler,
                cloudResourceAccessClientRepositoryPort,
                repository,
                groupQueryService,
                notificationsCommandService,
                new CloudResourceAccessFactory()
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
