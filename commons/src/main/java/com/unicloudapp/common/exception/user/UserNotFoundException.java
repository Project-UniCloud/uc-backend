package com.unicloudapp.common.exception.user;

import com.unicloudapp.common.vo.user.UserId;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UserId userId) {
        super("User with id %s not found".formatted(userId));
    }
}
