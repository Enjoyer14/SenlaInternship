package com.sental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sental.config.SecurityConfig;
import com.sental.config.TestApplicationConfig;
import com.sental.config.TestPersistenceConfig;
import com.sental.config.WebConfig;
import com.sental.dto.request.create.UserCreateRequest;
import com.sental.dto.request.update.UserUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        TestPersistenceConfig.class,
        TestApplicationConfig.class,
        WebConfig.class,
        SecurityConfig.class
})
@TestPropertySource(properties = {
        "jwt.secret=test-secret-key-test-secret-key-test-secret-key-123456",
        "jwt.expiration=3600000"
})
@WebAppConfiguration
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .apply(springSecurity())
                .build();
        this.objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void healthCheck_ShouldReturnOkStatus() throws Exception {
        mockMvc.perform(get("/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("UserController is working!"));
    }

    @Test
    void create_ShouldCreateUserAndReturn201() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail("john.doe@example.com");
        request.setUsername("johndoe");
        request.setPassword("securePassword123");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")))
                .andExpect(jsonPath("$.username", is("johndoe")))
                .andExpect(jsonPath("$.role", is("USER")))
                .andExpect(jsonPath("$.createdAt", notNullValue()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_ShouldReturnUser() throws Exception {
        Long createdId = createUserInDb("jane.doe@example.com", "janedoe");

        mockMvc.perform(get("/users/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdId.intValue())))
                .andExpect(jsonPath("$.email", is("jane.doe@example.com")))
                .andExpect(jsonPath("$.username", is("janedoe")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findAll_ShouldReturnListOfUsers() throws Exception {
        createUserInDb("user1@example.com", "user1");
        createUserInDb("user2@example.com", "user2");

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[*].username", hasItems("user1", "user2")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_ShouldUpdateUserAndReturn200() throws Exception {
        Long createdId = createUserInDb("old.email@example.com", "oldusername");
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        updateRequest.setEmail("new.email@example.com");
        updateRequest.setUsername("newusername");
        updateRequest.setPassword("newPassword456");

        mockMvc.perform(put("/users/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdId.intValue())))
                .andExpect(jsonPath("$.email", is("new.email@example.com")))
                .andExpect(jsonPath("$.username", is("newusername")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_ShouldDeleteUserAndReturn204() throws Exception {
        Long createdId = createUserInDb("to.delete@example.com", "todelete");

        mockMvc.perform(delete("/users/{id}", createdId)).andExpect(status().isNoContent());

        mockMvc.perform(get("/users/{id}", createdId)).andExpect(status().isNotFound());
    }

    private Long createUserInDb(String email, String username) throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setEmail(email);
        request.setUsername(username);
        request.setPassword("pass");

        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        return objectMapper.readTree(jsonResponse).get("id").asLong();
    }
}