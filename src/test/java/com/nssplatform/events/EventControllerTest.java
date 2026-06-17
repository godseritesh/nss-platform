package com.nssplatform.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Optional;

import org.junit.Before;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

import org.springframework.jdbc.BadSqlGrammarException;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class EventControllerTest {

    // Your class to be tested will be placed here

    @Before
    public void setup() {
        // Initialize Mockito before each test
        Mockito.reset();
    }

    @Test
    public void testEventCreationWithValidInput() {
        // Arrange
        EventController eventController = new EventController();

        // Act
        // Test event creation with valid input
        Event event = new Event();
        event.setName("Event Name");
        event.setDescription("Event Description");

        try {
            Event createdEvent = eventController.createEvent(event);
            assertEquals("Event Name", createdEvent.getName());
        } catch (Exception e) {
            // Handle the exception
        }
    }

    @Test
    public void testEventCreationWithInvalidInput_NoName() {
        //Arrange
        EventController eventController = new EventController();

        // Act
        // Test event creation with invalid input - No name
        Event event = new Event();
        event.setDescription("Event Description");

        // Assert
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () ->
                eventController.createEvent(event));
        assertEquals("name", exception.getConstraintViolations().iterator().next().getPropertyPath().toString());
    }

    @Test
    public void testEventCreationWithInvalidInput_NoDescription() {
        //Arrange
        EventController eventController = new EventController();

        // Act
        // Test event creation with invalid input - No description
        Event event = new Event();
        event.setName("Event Name");

        // Assert
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () ->
                eventController.createEvent(event));
        assertEquals("description", exception.getConstraintViolations().iterator().next().getPropertyPath().toString());
    }

    @Test
    public void testEventReading() {
        // Act
        EventController eventController = new EventController();
        Event event = eventController.getEvent("Event ID");

        // Assert
        assertEquals("Event ID", event.getId());
    }

    @Test
    public void testEventReadingWithInvalidId() {
        // Arrange
        EventController eventController = new EventController();

        // Act
        // Test event reading with an invalid id
        eventController.getEvent("Invalid Event ID");

        // Assert
        BadSqlGrammarException exception = assertThrows(BadSqlGrammarException.class, () ->
                eventController.getEvent("Invalid Event ID"));
        assertEquals("Invalid Event ID", exception.getMessage());
    }

    @Test
    public void testEventUpdating() {
        // Act
        EventController eventController = new EventController();
        Event event = eventController.updateEvent("Event ID", new Event());

        // Assert
        assertEquals("Event ID", event.getId());
    }

    @Test
    public void testEventUpdatingWithInvalidId() {
        // Arrange
        EventController eventController = new EventController();

        // Act
        // Test event updating with an invalid id
        Event event = eventController.updateEvent("Invalid Event ID", new Event());

        // Assert
        BadSqlGrammarException exception = assertThrows(BadSqlGrammarException.class, () ->
                eventController.updateEvent("Invalid Event ID", new Event()));
        assertEquals("Invalid Event ID", exception.getMessage());
    }

    @Test
    public void testEventDeletion() {
        // Act
        EventController eventController = new EventController();
        eventController.deleteEvent("Event ID");

        // Assert
        assertDoesNotThrow(() -> eventController.getEvent("Event ID"));
    }
}

class Event {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

class EventController {

    // Implement business logic here

    public Event createEvent(Event event) throws Exception {
        return eventService.createEvent(event);
    }

    public Event getEvent(String id) {
        return eventService.getEvent(id);
    }

    public Event updateEvent(String id, Event event) {
        return eventService.updateEvent(id, event);
    }

    public void deleteEvent(String id) {
        eventService.deleteEvent(id);
    }

}