package com.unicloudapp.auth.application.port.out;

public interface AuthenticationPort {

    boolean authenticate(String username, String password);
}
