package com.unicloudapp.common.vo.cloud;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

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
