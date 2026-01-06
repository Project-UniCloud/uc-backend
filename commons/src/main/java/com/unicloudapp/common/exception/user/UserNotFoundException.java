package com.unicloudapp.common.exception.user;

import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.common.vo.user.UserLogin;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(UserId userId) {
        super("User with id %s not found".formatted(userId));
    }

    public UserNotFoundException(UserLogin userLogin) {
        super("User with login %s not found".formatted(userLogin));
    }
}
