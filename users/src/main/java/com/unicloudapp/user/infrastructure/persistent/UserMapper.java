package com.unicloudapp.user.infrastructure.persistent;

import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class UserMapper {

    UserEntity userToEntity(User user) {
        return UserEntity.builder()
                .uuid(user.getUserId().getValue())
                .firstName(user.getFirstName().getValue())
                .lastName(user.getLastName().getValue())
                .email(user.getEmail().getValue())
                .role(user.getUserRole().getUserRoleType())
                .lastLogin(Objects.requireNonNullElse(user.getLastLogin()
                        .getLastLoginAt(), null))
                .build();
    }

    User entityToUser(UserEntity userEntity) {
        return UserFactory.createUser(
                userEntity.getUuid(),
                userEntity.getIndexNumber(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getRole(),
                userEntity.getEmail(),
                userEntity.getLastLogin()
        );
    }
}
