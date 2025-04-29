package com.unicloudapp.cloudmanagment.application;

import com.unicloudapp.users.application.UserDTO;
import com.unicloudapp.users.domain.User;

public interface CloudAccessClientPort {

    boolean isRunning();

    User createUser(UserDTO userDTO);

    void shutdown();
}
