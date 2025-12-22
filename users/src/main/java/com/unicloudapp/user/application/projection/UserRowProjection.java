package com.unicloudapp.user.application.projection;

import com.unicloudapp.common.vo.user.UserRole;

import java.util.Set;
import java.util.UUID;

public interface UserRowProjection {
    UUID getUuid();
    String getEmail();
    String getFirstName();
    String getLastName();
    String getLogin();
    Set<UserRole.Type> getRoles();
}
