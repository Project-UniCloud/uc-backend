package com.unicloudapp.common.user;

import com.unicloudapp.common.domain.user.UserId;

public interface UserValidationService {

    boolean isUserStudent(UserId userId);

    boolean isUserExists(UserId userId);
}
