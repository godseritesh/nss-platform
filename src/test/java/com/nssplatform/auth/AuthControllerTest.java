package com.nssplatform.auth;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestException;

import static org.mockito.Mockito.spy;

public class AuthControllerTest {
    
    @Test
    public void testInvalidAuthenticationRequest() throws Exception {
        AuthUser authUser = new AuthUser();
        authUser.setEmail("");
        Map<String, Object> response = new HashMap<>();
        String exceptionMessage = authUser.getEmail() + " is not a valid email";

        AuthController controller = spy(new AuthController(authService, userDetailsService));
        try {
            controller.register(authUser);
            throw new TestException("Should have thrown an exception");
        } catch (InvalidRequestException e) {
            Assert.assertEquals(e.getLocalizedMessage(), exceptionMessage);
            Assert.assertEquals(e.getValidationErrors().get("email"), List.of(exceptionMessage));
        }
    }
}