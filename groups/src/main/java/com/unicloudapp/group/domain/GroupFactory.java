package com.unicloudapp.group.domain;

public class GroupFactory {

    public static Group create() {
        return Group.builder()
                .groupId()
                .build();
    }
}
