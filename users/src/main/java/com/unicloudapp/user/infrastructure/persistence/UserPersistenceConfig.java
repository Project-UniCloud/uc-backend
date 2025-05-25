package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.domain.UserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserPersistenceConfig {

    @Bean
    UserRepositoryPort userRepositoryPort(
            UserRepositoryJpa userRepositoryJpa,
            UserMapper userMapper
    ) {
        return new SqlUserRepositoryAdapter(
            userRepositoryJpa, userMapper, new UserFactory()
        );
    }
}
