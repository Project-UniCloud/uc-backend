package com.unicloudapp.common.domain.cloud;

import lombok.Value;

@Value
public class CloudAccessClientId {

    String value;

    public static CloudAccessClientId of(String accessId) {
        if (accessId == null || accessId.isEmpty()) {
            throw new IllegalArgumentException("Access Id cannot be null or empty");
        }
        return new CloudAccessClientId(accessId);
    }
}
