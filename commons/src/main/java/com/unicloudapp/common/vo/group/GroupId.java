package com.unicloudapp.common.vo.group;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupId {

    UUID uuid;

    public static GroupId of(UUID uuid) {
        return new GroupId(uuid);
    }
}
