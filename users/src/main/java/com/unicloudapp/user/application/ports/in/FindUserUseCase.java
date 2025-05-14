package com.unicloudapp.user.application.ports.in;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.domain.User;

public interface FindUserUseCase {

    User findUserById(UserId userId);
}
