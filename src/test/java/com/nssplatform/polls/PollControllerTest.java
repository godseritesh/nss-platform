package com.nssplatform.polls;

import com.nssplatform.polls.dto.PollRequest;
import com.nssplatform.polls.controller.PollController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PollControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PollController pollController;

    @Test
    void testValidInput() throws Exception {
        PollRequest request = new PollRequest();
        request.setName("Test Poll");
        request.setDescription("This is a test poll");

        mockMvc.perform(post("/polls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void testInvalidInput() throws Exception {
        PollRequest request = new PollRequest();
        request.setName(null);
        request.setDescription(null);

        mockMvc.perform(post("/polls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmptyInput() throws Exception {
        PollRequest request = new PollRequest();

        mockMvc.perform(post("/polls")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    private static String asJsonString(final Object obj) {
        return "{\"name\":\"" + obj + "\",\"description\":\"" + obj + "\"}";
    }
}