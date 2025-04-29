package com.unicloudapp.starter;

import com.unicloudapp.cloudmanagment.infrastructure.grpc.CloudAccessProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.unicloudapp")
@EnableJpaRepositories(basePackages = "com.unicloudapp")
@EntityScan(basePackages = "com.unicloudapp")
@EnableConfigurationProperties({CloudAccessProperties.class})
class UniCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniCloudApplication.class, args);
    }
}
