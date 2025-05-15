package com.unicloudapp.user.application.port.in;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.user.domain.User;

public interface FindUserUseCase {

    User findUserById(UserId userId);
}
