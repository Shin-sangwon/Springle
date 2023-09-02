package com.springle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringleApplication.class, args);
    }

}
