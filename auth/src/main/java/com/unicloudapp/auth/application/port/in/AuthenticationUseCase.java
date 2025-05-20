package com.unicloudapp.auth.application.port.in;

import com.unicloudapp.auth.application.AuthenticatedResult;

public interface AuthenticationUseCase {

    AuthenticatedResult authenticate(String username, String password);
}
