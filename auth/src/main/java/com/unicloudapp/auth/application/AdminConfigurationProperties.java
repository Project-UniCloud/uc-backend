package com.unicloudapp.auth.application;

import com.unicloudapp.common.auth.AdminProperties;
import com.unicloudapp.common.vo.user.UserLogin;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "auth")
record AdminConfigurationProperties(List<String> admins) implements AdminProperties {

    @Override
    public List<UserLogin> getAdmins() {
        return admins.stream().map(UserLogin::of).toList();
    }
}
