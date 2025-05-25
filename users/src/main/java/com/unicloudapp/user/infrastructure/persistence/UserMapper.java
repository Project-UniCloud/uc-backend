package com.unicloudapp.user.infrastructure.persistence;

import com.unicloudapp.user.domain.User;
import com.unicloudapp.user.domain.UserFactory;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserFactory.class)
interface UserMapper {

    @Mapping(source = "userId.value", target = "uuid")
    @Mapping(source = "userLogin.value", target = "login")
    @Mapping(source = "firstName.value", target = "firstName")
    @Mapping(source = "lastName.value", target = "lastName")
    @Mapping(source = "userRole.value", target = "role")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "lastLoginAt.value", target = "lastLogin")
    UserEntity userToEntity(User user);

    @Mapping(target = "user", ignore = true)
    default User entityToUser(UserEntity userEntity, @Context UserFactory userFactory) {
        if (userEntity == null) {
            return null;
        }
        return userFactory.restore(
                userEntity.getUuid(),
                userEntity.getLogin(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getRole(),
                userEntity.getEmail(),
                userEntity.getLastLogin()
        );
    }
}
