package com.society;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan("com.society.entity")
@EnableJpaRepositories("com.society.repository")
@EnableScheduling
public class SocietyManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(SocietyManagementApplication.class, args);
    }
}
