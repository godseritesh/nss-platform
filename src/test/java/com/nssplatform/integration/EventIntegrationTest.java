package com.nssplatform.integration;

import com.nssplatform.model.Event;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void eventCreateWithValidData_ReturnsOk() throws Exception {
        // Arrange
        Event event = new Event();
        event.setName("TestEvent");

        // Act and Assert
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(event)))
                .andExpect(status().isOk());
    }

    @Test
    public void eventCreateWithInvalidName_ReturnsBadRequest() throws Exception {
        // Arrange
        Event event = new Event();
        event.setName(null);

        // Act and Assert
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(event)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void eventCreateWithEmptyName_ReturnsBadRequest() throws Exception {
        // Arrange
        Event event = new Event();
        event.setName("");

        // Act and Assert
        mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(event)))
                .andExpect(status().isBadRequest());
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}