package com.nssplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class NssPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(NssPlatformApplication.class, args);
    }
}
