package com.sources.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.sources.entities")
@ComponentScan(basePackages = "com.sources")
@EnableJpaRepositories(basePackages = "com.sources.repositories")
public class ImageUrLsProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageUrLsProjectApplication.class, args);
    }

}
