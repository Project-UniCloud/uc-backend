package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.group.domain.GroupFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SqlGroupPersistentConfig {

    @Bean
    GroupToEntityMapper groupMapper() {
        return new GroupToEntityMapper(new GroupFactory());
    }
}
