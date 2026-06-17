package com.nssplatform.auth;

import com.nssplatform.user.exception.InvalidRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nssplatform.user.model.AuthUser;
import com.nssplatform.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;

import springfox.documentation.spring.web.json.Json;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @MockBean
    private AuthService authService;

    @MockBean
    private UserDetailsService userDetailsService;

    private MockMvc mvc;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new AuthController()).build();
    }

    @Test
    public void testInvalidRegisterRequest() throws Exception {
        AuthUser authUser = new AuthUser();
        authUser.setEmail("");
        Map<String, Object> response = new HashMap<>();
        String exceptionMessage = authUser.getEmail() + " is not a valid email";

        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(authUser)))
                .andExpect(status().isBadRequest())
                .andExpect(mvcResult -> {
                    Map<String, Object> responseMap = (Map<String, Object>) mvcResult.getResolvedModelAndView().getModelMap().get("error");
                    Assert.assertEquals(responseMap.get("email"), exceptionMessage);
                    Assert.assertEquals(((HashMap<String, List<String>>) responseMap.get("errorMessages")).get("email"), List.of(exceptionMessage));
                });
    }

    @Test
    public void testValidRegisterRequest() throws Exception {
        AuthUser authUser = new AuthUser();
        authUser.setEmail("user@example.com");
        mvc.perform(MockMvcRequestBuilders.post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new Gson().toJson(authUser)))
                .andExpect(status().isOk());
    }

    @Test
    // Other tests as per business requirements
}