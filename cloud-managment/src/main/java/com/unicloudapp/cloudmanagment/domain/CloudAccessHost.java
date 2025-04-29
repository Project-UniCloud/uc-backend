package com.unicloudapp.cloudmanagment.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CloudAccessHost {

    private final String host;

    public static CloudAccessHost of(String host) {
        return new CloudAccessHost(host);
    }
}
