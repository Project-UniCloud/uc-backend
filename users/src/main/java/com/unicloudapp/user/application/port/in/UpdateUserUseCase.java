package com.unicloudapp.user.application.port.in;

import com.unicloudapp.user.application.command.UpdateUserCommand;

public interface UpdateUserUseCase {
    void updateUser(UpdateUserCommand updateUserCommand);
}
