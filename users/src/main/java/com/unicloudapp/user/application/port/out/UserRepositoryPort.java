package com.unicloudapp.user.application.port.out;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.domain.User;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UserId userId);

    boolean existsById(UserId userId);

    boolean existsByLogin(String login);
}
