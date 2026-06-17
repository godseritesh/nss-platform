package com.nssplatform.integration;

import com.nssplatform.model.Poll;
import com.nssplatform.repository.PollRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
public class PollIntegrationTest {

    @Autowired
    private PollRepository pollRepository;

    @Test
    void testInputValidation() {
        Poll poll = new Poll();
        poll.setTitle(null);
        poll.setDescription(null);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> pollRepository.save(poll));
        System.out.println(exception.getMessage());
    }
}