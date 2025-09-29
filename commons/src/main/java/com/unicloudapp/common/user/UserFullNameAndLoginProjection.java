package com.unicloudapp.common.user;

import java.util.UUID;

public interface UserFullNameAndLoginProjection {

    UUID getUuid();

    String getFirstName();

    String getLastName();

    String getLogin();

    String getEmail();
}
