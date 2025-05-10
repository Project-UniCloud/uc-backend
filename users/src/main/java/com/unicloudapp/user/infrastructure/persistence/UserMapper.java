package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class UserMapper {

    private final UserFactory userFactory;

    UserEntity userToEntity(User user) {
        return UserEntity.builder()
                .uuid(user.getUserId().getValue())
                .firstName(user.getFirstName().getValue())
                .lastName(user.getLastName().getValue())
                .indexNumber(user.getUserLogin().getValue())
                .email(user.getEmail().getValue())
                .role(user.getUserRole().getValue())
                .lastLogin(user.getLastLoginAt().getValue())
                .build();
    }

    User entityToUser(UserEntity userEntity) {
        return userFactory.restore(
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
