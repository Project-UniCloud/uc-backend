package com.unicloudapp.user.application;

import com.unicloudapp.common.auth.AdminProperties;
import com.unicloudapp.user.application.port.out.UserRepositoryPort;
import com.unicloudapp.user.domain.UserFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class UserConfig {

    @Bean
    UserService userService(UserRepositoryPort userRepository, AdminProperties adminProperties) {
        return new UserService(userRepository, new UserFactory(), adminProperties);
    }
}
