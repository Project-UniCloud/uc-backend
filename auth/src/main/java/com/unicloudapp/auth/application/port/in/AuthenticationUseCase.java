package com.unicloudapp.auth.application.port.in;

public interface AuthenticationUseCase {

    String authenticate(String username, String password);
}
