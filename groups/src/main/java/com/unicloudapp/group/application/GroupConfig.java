package com.unicloudapp.group.application;

import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.user.UserCommandService;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.user.UserValidationService;
import com.unicloudapp.group.domain.GroupFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GroupConfig {

    @Bean
    GroupService getGroupFactory(
            GroupRepositoryPort groupRepository,
            GroupToDtoMapper groupToDtoMapper,
            UserValidationService userValidationService,
            UserQueryService userQueryService,
            CloudResourceAccessQueryService cloudResourceAccessQueryService,
            CloudResourceAccessCommandService cloudResourceAccessCommandService,
            UserCommandService userCommandService
    ) {
        return new GroupService(
                groupRepository,
                new GroupFactory(),
                groupToDtoMapper,
                userValidationService,
                userQueryService,
                cloudResourceAccessQueryService,
                cloudResourceAccessCommandService,
                userCommandService
        );
    }
}
