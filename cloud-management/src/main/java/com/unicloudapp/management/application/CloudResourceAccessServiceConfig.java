package com.unicloudapp.management.application;

import com.unicloudapp.common.group.GroupQueryService;
import com.unicloudapp.common.notifications.NotificationsCommandService;
import com.unicloudapp.management.application.port.CloudResourceAccessClientRepositoryPort;
import com.unicloudapp.management.application.port.CloudResourceAccessRepositoryPort;
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
            CloudResourceAccessClientRepositoryPort CloudResourceAccessClientRepositoryPort
    ) {
        return new CloudResourceAccessService(taskScheduler, CloudResourceAccessClientRepositoryPort, repository, groupQueryService, notificationsCommandService);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5);
        scheduler.setThreadNamePrefix("cloud-cleaner-");
        return scheduler;
    }
}
