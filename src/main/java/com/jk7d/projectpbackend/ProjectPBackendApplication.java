package com.jk7d.projectpbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class ProjectPBackendApplication {

    public static void main(final String[] args) {
        SpringApplication.run(ProjectPBackendApplication.class, args);
    }

}
