package com.kostapo.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
public class BotApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }




}
