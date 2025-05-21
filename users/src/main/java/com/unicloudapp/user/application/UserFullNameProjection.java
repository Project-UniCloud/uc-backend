package com.unicloudapp.user.application;

import java.util.UUID;

public interface UserFullNameProjection {

    UUID getUuid();

    String getFirstName();

    String getLastName();
}
