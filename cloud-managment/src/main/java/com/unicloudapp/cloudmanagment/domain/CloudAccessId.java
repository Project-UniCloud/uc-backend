package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudAccessId {

    UUID value;

    public static CloudAccessId of(UUID value) {
        return new CloudAccessId(value);
    }
}
