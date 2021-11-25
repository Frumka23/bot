package com.kostapo.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.logging.LogManager;

@SpringBootApplication
@EnableScheduling
public class BotApplication implements WebMvcConfigurer {

    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(
                    BotApplication.class.getResourceAsStream("/logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }
        SpringApplication.run(BotApplication.class, args);
    }




}
