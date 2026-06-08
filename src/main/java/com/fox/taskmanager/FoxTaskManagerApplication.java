package com.fox.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FoxTaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoxTaskManagerApplication.class, args);
    }
}
