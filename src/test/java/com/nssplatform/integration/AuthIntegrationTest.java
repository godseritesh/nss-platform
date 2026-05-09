package com.nssplatform.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nssplatform.auth.dto.LoginRequest;
import com.nssplatform.auth.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void registerAndLogin() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setName("Integration Tester");
        reg.setEmail("inttest@nss.test");
        reg.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("inttest@nss.test"))
            .andExpect(jsonPath("$.role").value("ROLE_USER"));

        LoginRequest login = new LoginRequest();
        login.setEmail("inttest@nss.test");
        login.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("inttest@nss.test"));
    }

    @Test
    void invalidLoginReturns401() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("nobody@nss.test");
        login.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void registerWithDuplicateEmailReturns409() throws Exception {
        RegisterRequest reg = new RegisterRequest();
        reg.setName("Dup User");
        reg.setEmail("dup@nss.test");
        reg.setPassword("Password123!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)))
            .andExpect(status().isConflict());
    }

    @Test
    void adminEndpointRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/admin/analytics/overview"))
            .andExpect(status().isForbidden());
    }

    @Test
    void publicEventsEndpointOpen() throws Exception {
        mockMvc.perform(get("/api/events"))
            .andExpect(status().isOk());
    }

    @Test
    void invalidRegisterPayloadReturns400() throws Exception {
        RegisterRequest bad = new RegisterRequest();
        bad.setName("");
        bad.setEmail("not-an-email");
        bad.setPassword("short");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bad)))
            .andExpect(status().isBadRequest());
    }
}
