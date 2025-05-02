package com.unicloudapp.group.domain;

import lombok.Value;

import java.util.UUID;

@Value
public class GroupId {

    UUID uuid;

    public static GroupId of(UUID uuid) {
        return new GroupId(uuid);
    }
}
