package com.unicloudapp.user.application.projection;

import java.util.UUID;

public interface UserRowProjection {

    UUID getUuid();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getLogin();
}
