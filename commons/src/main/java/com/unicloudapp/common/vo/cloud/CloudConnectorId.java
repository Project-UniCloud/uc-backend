package com.unicloudapp.common.vo.cloud;

public record CloudConnectorId(String id) {

    public static CloudConnectorId of(String accessId) {
        if (accessId == null || accessId.isEmpty()) {
            throw new IllegalArgumentException("Access Id cannot be null or empty");
        }
        return new CloudConnectorId(accessId);
    }
}
