package com.unicloudapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AdapterProperties.class)
class UniCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniCloudApplication.class, args);
    }
}
