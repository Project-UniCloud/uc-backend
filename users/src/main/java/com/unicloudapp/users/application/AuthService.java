package com.unicloudapp.users.application;

import com.unicloudapp.users.application.ports.out.AuthenticationPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationPort authenticationPort;

    public void authenticate(String username, String password) {
        authenticationPort.authenticate(username, password);
    }
}
