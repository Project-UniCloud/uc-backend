package com.unicloudapp.common.vo.cloud;

public record CloudVendorConnectorId(String id) {

    public static CloudVendorConnectorId of(String accessId) {
        if (accessId == null || accessId.isEmpty()) {
            throw new IllegalArgumentException("Access Id cannot be null or empty");
        }
        return new CloudVendorConnectorId(accessId);
    }
}
