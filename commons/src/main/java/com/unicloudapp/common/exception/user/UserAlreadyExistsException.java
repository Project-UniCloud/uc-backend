package com.unicloudapp.common.exception.user;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String login) {
        super(String.format("User with login: %s already exists", login));
    }
}
