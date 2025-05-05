package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.user.domain.UserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserPersistenceConfig {

    @Bean
    UserMapper userMapper() {
        return new UserMapper(new UserFactory());
    }
}
