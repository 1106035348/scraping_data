package com.data.scrapingdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ScrapingDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrapingDataApplication.class, args);
    }

}
