package com.unicloudapp.group.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupName {

    String name;

    public static GroupName of(String name) {
        return new GroupName(name);
    }
}
