package com.unicloudapp.users.application.ports.out;

import com.unicloudapp.users.domain.User;
import com.unicloudapp.users.domain.UserId;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UserId userId);
}
