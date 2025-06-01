package com.unicloudapp.common.domain.group;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupId {

    UUID uuid;

    public static GroupId of(UUID uuid) {
        return new GroupId(uuid);
    }
}
