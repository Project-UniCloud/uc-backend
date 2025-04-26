package com.unicloudapp.users.application.ports.out;

import com.unicloudapp.users.domain.User;
import com.unicloudapp.users.domain.UserId;

public interface UserRepositoryPort {

    User save(User user);

    User findById(UserId userId);
}
