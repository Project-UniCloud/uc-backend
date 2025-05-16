package com.unicloudapp.user.infrastructure.rest;

import com.unicloudapp.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface UserToUserFoundResponseMapper {

    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "userLogin.value", target = "login")
    @Mapping(source = "firstName.value", target = "firstName")
    @Mapping(source = "lastName.value", target = "lastName")
    @Mapping(source = "userRole.value", target = "userRole")
    @Mapping(source = "email.value", target = "email")
    @Mapping(source = "lastLoginAt.value", target = "lastLoginAt")
    UserFoundResponse toUserFoundResponse(User user);
}
