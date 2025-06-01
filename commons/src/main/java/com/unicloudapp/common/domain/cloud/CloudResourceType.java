package com.unicloudapp.common.domain.cloud;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudResourceType {

    String name;

    public static CloudResourceType of(String name) {
        return new CloudResourceType(name);
    }
}
