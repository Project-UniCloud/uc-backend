package com.unicloudapp.common.domain.cloud;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudAccessClientId {

    String value;

    public static CloudAccessClientId of(String accessId) {
        if (accessId == null || accessId.isEmpty()) {
            throw new IllegalArgumentException("Access Id cannot be null or empty");
        }
        return new CloudAccessClientId(accessId);
    }
}
