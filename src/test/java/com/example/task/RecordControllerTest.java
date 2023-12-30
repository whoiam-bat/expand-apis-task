package com.example.task;

import com.example.task.model.dto.AuthRequest;
import com.example.task.model.dto.AuthResponse;
import com.example.task.model.dto.Request;
import com.example.task.model.dto.RequestRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RecordControllerTest {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static HttpHeaders headers;


    @BeforeEach
    void setUp() {
        if (headers == null) {
            restTemplate.exchange("/user/add",
                    HttpMethod.POST,
                    new HttpEntity<>(AuthRequest.builder()
                            .username("user")
                            .password("password")
                            .build()
                    ),
                    AuthResponse.class
            );

            ResponseEntity<AuthResponse> resp = restTemplate.exchange("/user/authenticate",
                    HttpMethod.POST,
                    new HttpEntity<>(
                            AuthRequest.builder()
                                    .username("user")
                                    .password("password")
                                    .build()
                    ),
                    AuthResponse.class
            );

            headers = new HttpHeaders();

            headers.setBearerAuth(Objects.requireNonNull(resp.getBody()).getToken());
        }
    }


    private Request getRequestEntity() {
        return Request.builder()
                .table("products")
                .records(List.of(RequestRecord.builder()
                        .entryDate("03-01-2023")
                        .itemCode("11111")
                        .itemName("Test Inventory 1")
                        .itemQuantity("20")
                        .status("Paid")
                        .build()))
                .build();
    }


    @Test
    public void testFindAllWhenTableNotExists_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/products/all").headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddRecordsWithAuthorization_shouldReturnOk() throws Exception {
        Request request = getRequestEntity();

        mockMvc.perform(post("/products/add")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddRecordsWithoutAuthorization_shouldReturnForbidden() throws Exception {
        Request request = getRequestEntity();

        mockMvc.perform(post("/products/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testFindAllWithAuthorization_shouldReturnOk() throws Exception {
        Request request = getRequestEntity();

        mockMvc.perform(post("/products/add")
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/products/all").headers(headers))
                .andExpect(status().isOk());
    }

}


