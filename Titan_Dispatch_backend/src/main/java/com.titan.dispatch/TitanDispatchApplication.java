package com.titan.dispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TitanDispatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(TitanDispatchApplication.class, args);
    }
}