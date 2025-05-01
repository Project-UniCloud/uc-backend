package com.unicloudapp.cloudmanagment.domain;

import lombok.Value;

import java.util.UUID;

@Value
public class CloudAccessId {

    UUID value;

    public static CloudAccessId of(UUID value) {
        return new CloudAccessId(value);
    }
}
