package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CloudAccessPort {

    private final int port;

    public static CloudAccessPort of(int port) {
        return new CloudAccessPort(port);
    }
}
