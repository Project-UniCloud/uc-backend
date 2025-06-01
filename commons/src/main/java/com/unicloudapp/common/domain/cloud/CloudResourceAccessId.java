package com.unicloudapp.common.domain.cloud;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudResourceAccessId {

    UUID value;

    public static CloudResourceAccessId of(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        return new CloudResourceAccessId(id);
    }
}
