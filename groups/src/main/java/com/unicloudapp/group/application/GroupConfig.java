package com.unicloudapp.group.application;

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
            UserValidationService userValidationService
    ) {
        return new GroupService(groupRepository, new GroupFactory(), groupToDtoMapper, userValidationService);
    }
}
