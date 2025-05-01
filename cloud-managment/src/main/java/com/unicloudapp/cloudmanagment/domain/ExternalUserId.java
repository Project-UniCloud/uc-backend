package com.unicloudapp.cloudmanagment.domain;

import lombok.Value;

@Value
public class ExternalUserId {

    String userId;

    public static ExternalUserId of(String id) {
        return new ExternalUserId(id);
    }
}
