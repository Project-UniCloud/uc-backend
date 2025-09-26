package com.unicloudapp.auth.application.port.out;

import com.unicloudapp.common.domain.user.UserRole;

public interface AuthenticationProviderPort {

    UserRole authenticate(String username, String password);
}
