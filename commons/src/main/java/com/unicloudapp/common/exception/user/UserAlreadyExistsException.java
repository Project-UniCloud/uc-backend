package com.unicloudapp.common.exception.user;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String login) {
        super("User with login: %s already exists".formatted(login));
    }
}
