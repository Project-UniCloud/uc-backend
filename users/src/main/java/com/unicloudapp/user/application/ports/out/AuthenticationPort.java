package com.unicloudapp.user.application.ports.out;

public interface AuthenticationPort {

    void authenticate(String username, String password);
}
