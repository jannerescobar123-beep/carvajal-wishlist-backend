package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.AuthRequestDTO;
import com.carvajal.wishlist.dto.UserDTO;
import com.carvajal.wishlist.entity.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerAndLogin_Success() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("integrationUser");
        userDTO.setEmail("integration@example.com");
        userDTO.setPassword("password123");
        userDTO.setRole(Role.CLIENT);

        String userJson = "{\"username\":\"integrationUser\",\"email\":\"integration@example.com\",\"password\":\"password123\",\"role\":\"CLIENT\"}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.username").value("integrationUser"));

        AuthRequestDTO loginRequest = new AuthRequestDTO("integrationUser", "password123");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void register_duplicateUsername_throwsBadRequest() throws Exception {
        String userJson = "{\"username\":\"dupUser\",\"email\":\"dup1@example.com\",\"password\":\"password123\",\"role\":\"CLIENT\"}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());

        String duplicateJson = "{\"username\":\"dupUser\",\"email\":\"dup2@example.com\",\"password\":\"password123\",\"role\":\"CLIENT\"}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_duplicateEmail_throwsBadRequest() throws Exception {
        String userJson = "{\"username\":\"emailUser1\",\"email\":\"same@example.com\",\"password\":\"password123\",\"role\":\"CLIENT\"}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());

        String duplicateJson = "{\"username\":\"emailUser2\",\"email\":\"same@example.com\",\"password\":\"password123\",\"role\":\"CLIENT\"}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(duplicateJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_invalidCredentials_throwsUnauthorized() throws Exception {
        String userJson = "{\"username\":\"loginTest\",\"email\":\"login@test.com\",\"password\":\"password123\",\"role\":\"CLIENT\"}";

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated());

        AuthRequestDTO badLogin = new AuthRequestDTO("loginTest", "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized());
    }
}
