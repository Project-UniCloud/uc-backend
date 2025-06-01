package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudUserId {

    String userId;

    public static CloudUserId of(String id) {
        return new CloudUserId(id);
    }
}
