package com.unicloudapp.group.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupStatus {

    Type status;

    public static GroupStatus of(Type status) {
        return new GroupStatus(status);
    }

    public boolean isActive() {
        return status == Type.ACTIVE;
    }

    public enum Type {
        ACTIVE, INACTIVE, ARCHIVED
    }
}
