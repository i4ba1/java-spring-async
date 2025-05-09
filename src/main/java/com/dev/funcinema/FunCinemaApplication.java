package com.dev.funcinema;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;

@Async
@SpringBootApplication
public class FunCinemaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FunCinemaApplication.class, args);
    }

}
