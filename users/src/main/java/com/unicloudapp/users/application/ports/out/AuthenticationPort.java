package com.unicloudapp.users.application.ports.out;

public interface AuthenticationPort {

    void authenticate(String username, String password);
}
