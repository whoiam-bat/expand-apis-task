package com.example.task;

import com.example.task.model.dto.AuthRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private AuthRequest authRequest;


    @BeforeEach
    void setUp() {
        this.authRequest = getUserInstance();
    }

    private AuthRequest getUserInstance() {
        return AuthRequest.builder()
                .username("user")
                .password("root")
                .build();
    }

    private ResultActions addUser(AuthRequest authRequest) throws Exception {

        return mockMvc.perform(post("/user/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)));
    }


    @Test
    void testAddNewUser_shouldReturnOk() throws Exception {
        addUser(authRequest).andExpect(status().isOk());
    }

    @Test
    void testAddExistingUser_shouldReturnConflict() throws Exception {
        addUser(authRequest);

        addUser(authRequest).andExpect(status().isConflict());
    }

    @Test
    void testAuthenticate_shouldReturnOk() throws Exception {
        addUser(authRequest);

        mockMvc.perform(post("/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());
    }


    @Test
    void testAuthenticate_shouldReturnNotFound() throws Exception {
        addUser(authRequest);

        authRequest.setUsername("sample_user");

        mockMvc.perform(post("/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAuthenticate_shouldReturnForbidden() throws Exception {
        addUser(authRequest);

        authRequest.setUsername("sample_password");

        mockMvc.perform(post("/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isNotFound());
    }

}
