package com.unicloudapp.auth.application.port.out;

public interface AuthenticationProviderPort {

    boolean authenticate(String username, String password);
}
