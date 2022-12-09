package com.sparta.spring_hw_memo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringHwMemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringHwMemoApplication.class, args);
    }

}
