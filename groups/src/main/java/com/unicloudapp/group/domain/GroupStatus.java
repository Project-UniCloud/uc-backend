package com.unicloudapp.group.domain;

import lombok.Value;

@Value
public class GroupStatus {

    Type status;

    public static GroupStatus of(Type status) {
        return new GroupStatus(status);
    }

    public boolean isActive() {
        return status == Type.ACTIVE;
    }

    public enum Type {
        ACTIVE, INACTIVE
    }
}
