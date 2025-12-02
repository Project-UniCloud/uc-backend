package com.unicloudapp.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

@SpringBootApplication(scanBasePackages = "com.unicloudapp")
@EnableJpaRepositories(basePackages = "com.unicloudapp")
@EntityScan(basePackages = "com.unicloudapp")
@ConfigurationPropertiesScan("com.unicloudapp")
@EnableScheduling
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
class UniCloudApplication {

    static void main(String[] args) {
        SpringApplication.run(UniCloudApplication.class, args);
    }
}
