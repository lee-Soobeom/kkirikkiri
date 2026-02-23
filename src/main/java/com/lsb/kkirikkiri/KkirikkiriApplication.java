package com.lsb.kkirikkiri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KkirikkiriApplication {

    public static void main(String[] args) {
        SpringApplication.run(KkirikkiriApplication.class, args);
    }

}
