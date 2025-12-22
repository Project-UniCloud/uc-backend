package com.unicloudapp.common.vo.user;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Set;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserRole {

    Set<Type> roles;

    public static UserRole of(Type... userTypes) {
        if (userTypes == null || userTypes.length == 0) {
            throw new IllegalArgumentException();
        }
        return new UserRole(Set.of(userTypes));
    }

    public static UserRole of(Set<Type> userTypes) {
        if (userTypes == null || userTypes.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return new UserRole(Set.copyOf(userTypes));
    }

    public boolean hasRole(Type role) {
        return roles.contains(role);
    }

    public enum Type {
        ADMIN, STUDENT, LECTURER
    }
}
