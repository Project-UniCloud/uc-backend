package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CloudAccessId {

    private final String accessId;

    public static CloudAccessId of(String accessId) {
        if (accessId == null || accessId.isEmpty()) {
            throw new IllegalArgumentException("Access Id cannot be null or empty");
        }
        return new CloudAccessId(accessId);
    }
}
