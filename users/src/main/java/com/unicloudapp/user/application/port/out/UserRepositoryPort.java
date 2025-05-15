package com.unicloudapp.user.application.port.out;

import com.unicloudapp.user.domain.User;
import com.unicloudapp.common.domain.user.UserId;

import java.util.Optional;

public interface UserRepositoryPort {

    User save(User user);

    Optional<User> findById(UserId userId);
}
