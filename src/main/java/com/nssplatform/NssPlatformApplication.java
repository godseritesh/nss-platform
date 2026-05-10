package com.nssplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class NssPlatformApplication {
    public static void main(String[] args) {
        SpringApplication.run(NssPlatformApplication.class, args);
    }
}
