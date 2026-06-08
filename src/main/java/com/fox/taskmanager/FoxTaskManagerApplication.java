package com.fox.taskmanager;

import com.fox.taskmanager.config.AdminProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AdminProperties.class)
public class FoxTaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoxTaskManagerApplication.class, args);
    }
}
