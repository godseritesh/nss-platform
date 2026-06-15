package com.nssplatform.shared.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class KeepAliveTask {

    private final RestTemplate restTemplate;
    private final int port;

    public KeepAliveTask(RestTemplate restTemplate, @Value("${server.port:8080}") int port) {
        this.restTemplate = restTemplate;
        this.port = port;
    }

    @Scheduled(fixedRate = 840_000)
    public void selfPing() {
        try {
            restTemplate.getForObject("http://localhost:" + port + "/ping", String.class);
        } catch (Exception e) {
            log.warn("Self-ping failed: {}", e.getMessage());
        }
    }
}
