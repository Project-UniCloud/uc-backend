package com.unicloudapp.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.unicloudapp")
@EnableJpaRepositories(basePackages = "com.unicloudapp")
@EntityScan(basePackages = "com.unicloudapp")
class UniCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniCloudApplication.class, args);
    }
}
