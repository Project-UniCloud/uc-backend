package com.unicloudapp.group.domain;

import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GroupStatus {

    Type status;

    public static GroupStatus of(Type status) {
        return new GroupStatus(status);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    public enum Type {

        ACTIVE("Aktywna"), INACTIVE("Nieaktywna"), ARCHIVED("Zarchiwizowana");

        private final String displayName;
    }
}
