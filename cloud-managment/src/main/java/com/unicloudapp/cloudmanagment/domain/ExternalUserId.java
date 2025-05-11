package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExternalUserId {

    String userId;

    public static ExternalUserId of(String id) {
        return new ExternalUserId(id);
    }
}
