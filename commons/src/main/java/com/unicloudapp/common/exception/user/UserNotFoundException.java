package com.unicloudapp.common.exception.user;

import com.unicloudapp.common.domain.user.UserId;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UserId userId) {
        super(String.format("User with id %s not found", userId));
    }
}
