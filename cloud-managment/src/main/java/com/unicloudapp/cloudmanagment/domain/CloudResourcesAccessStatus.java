package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CloudResourcesAccessStatus {

    Status status;

    public static CloudResourcesAccessStatus of(Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return new CloudResourcesAccessStatus(status);
    }

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}
