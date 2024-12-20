package com.universityweb.common.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.UserAlreadyExistsException;
import com.universityweb.common.auth.request.RegisterRequest;
import com.universityweb.common.auth.service.auth.AuthService;
import com.universityweb.common.media.service.MediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService; // Mock AuthService

    @Configuration
    static class TestConfig {
        @Bean
        public AuthService mediaService() {
            return mock(AuthService.class); // Create a mock instance
        }
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void testRegister_Success() throws Exception {
        // Mock request payload
        RegisterRequest request = new RegisterRequest(
                "john", "P@123456789", "John Doe", "john@gmail.com",
                "+84972640891", User.EGender.MALE, LocalDate.of(2000, 1, 1), null
        );

        // Mock service behavior
        doNothing().when(authService).registerStudentAccount(any(RegisterRequest.class));

        // Perform POST request
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))  // RequestBuilder provided here
                .andExpect(status().isCreated())                    // ResultMatcher applied here
                .andExpect(content().string("Register successfully"));

        // Verify AuthService interaction
        verify(authService, times(1)).registerStudentAccount(any(RegisterRequest.class));
    }

    @Test
    public void testRegister_UsernameAlreadyExists() throws Exception {
        // Mock request payload
        RegisterRequest request = new RegisterRequest(
                "john", "P@123456789", "John Doe", "john@gmail.com",
                "+84972640891", User.EGender.MALE, LocalDate.of(2000, 1, 1), null
        );

        // Mock service behavior to throw exception
        doThrow(new UserAlreadyExistsException("Username already exists"))
                .when(authService).registerStudentAccount(any(RegisterRequest.class));

        // Perform POST request
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already exists"));

        // Verify AuthService interaction
        verify(authService, times(1)).registerStudentAccount(any(RegisterRequest.class));
    }

}