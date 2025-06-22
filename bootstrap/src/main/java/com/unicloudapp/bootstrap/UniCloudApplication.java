package com.unicloudapp.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.unicloudapp")
@EnableJpaRepositories(basePackages = "com.unicloudapp")
@EntityScan(basePackages = "com.unicloudapp")
@ConfigurationPropertiesScan("com.unicloudapp")
@EnableScheduling
class UniCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniCloudApplication.class, args);
    }
}
