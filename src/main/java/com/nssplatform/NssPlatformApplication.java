package com.nssplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Arrays;

@SpringBootApplication
@EnableAsync
public class NssPlatformApplication {
    public static void main(String[] args) {
        String[] originalArgs = new String[args.length + 2];
        System.arraycopy(args, 0, originalArgs, 0, args.length);
        originalArgs[args.length] = "--spring.config.location";
        originalArgs[args.length + 1] = "classpath:/config";
        SpringApplication.run(NssPlatformApplication.class, originalArgs);
    }
}