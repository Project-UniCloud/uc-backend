package com.unicloudapp.group.domain;

import lombok.Value;

@Value
public class GroupName {

    String name;

    public static GroupName of(String name) {
        return new GroupName(name);
    }
}
