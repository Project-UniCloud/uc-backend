package com.unicloudapp.users.infrastructure.persistent;

import com.unicloudapp.commons.domain.Email;
import com.unicloudapp.commons.domain.FirstName;
import com.unicloudapp.commons.domain.LastLogin;
import com.unicloudapp.commons.domain.LastName;
import com.unicloudapp.users.domain.User;
import com.unicloudapp.users.domain.UserId;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
class UserMapper {

    UserEntity userToEntity(User user) {
        return UserEntity.builder()
                .id(user.getUserId().getValue())
                .firstName(user.getFirstName().getValue())
                .lastName(user.getLastName().getValue())
                .email(user.getEmail().getValue())
                .role(user.getUserRole())
                .lastLogin(Objects.requireNonNullElse(user.getLastLogin()
                        .getLastLoginAt(), null))
                .build();
    }

    User entityToUser(UserEntity userEntity) {
        return User.of(
                UserId.of(userEntity.getId()),
                FirstName.of(userEntity.getFirstName()),
                LastName.of(userEntity.getLastName()),
                userEntity.getRole(),
                Email.of(userEntity.getEmail()),
                LastLogin.of(userEntity.getLastLogin())
        );
    }
}
