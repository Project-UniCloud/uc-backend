package com.unicloudapp.user.application.projection;

import java.util.UUID;

public interface UserFullNameProjection {

    UUID getUuid();

    String getFirstName();

    String getLastName();
}
