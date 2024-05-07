package com.chris;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
@EnableWebSecurity(debug = true)
public class AuthEntryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthEntryServiceApplication.class, args);
    }

}
