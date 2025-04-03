package com.example.srmsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.example.srmsystem.repository")
public class SrmSystemApplication {
    public static void main(String[] args)   {
        SpringApplication.run(SrmSystemApplication.class, args);

    }
}
