package com.unicloudapp.user.application;

import com.unicloudapp.user.application.ports.out.AuthenticationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class AuthService {

    private final AuthenticationPort authenticationPort;

    public void authenticate(String username, String password) {
        authenticationPort.authenticate(username, password);
    }
}
