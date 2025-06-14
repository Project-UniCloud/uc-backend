package com.unicloudapp.group.application;

import com.unicloudapp.common.user.UserFullName;

import java.util.UUID;

record UserFullNameDTO(UUID userId, String firstName, String lastName) {

    static UserFullNameDTO from(UserFullName userFullName) {
        return new UserFullNameDTO(
                userFullName.userId().getValue(),
                userFullName.firstName().getValue(),
                userFullName.lastName().getValue()
        );
    }
}
