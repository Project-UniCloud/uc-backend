package com.unicloudapp.group.application;

import com.unicloudapp.group.domain.GroupFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class GroupConfig {

    @Bean
    GroupService getGroupFactory(GroupRepositoryPort groupRepository, GroupToDtoMapper groupToDtoMapper) {
        return new GroupService(groupRepository, new GroupFactory(), groupToDtoMapper);
    }
}
