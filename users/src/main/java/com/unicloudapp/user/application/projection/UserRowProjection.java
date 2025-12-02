package com.unicloudapp.user.application.projection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.UUID;

@JsonIgnoreProperties({"decoratedClass"})
public interface UserRowProjection {

    UUID getUuid();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getLogin();

    String getRole();
}
