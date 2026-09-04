package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.AuthRequestDTO;
import com.carvajal.wishlist.dto.AuthResponseDTO;
import com.carvajal.wishlist.dto.UserDTO;
import com.carvajal.wishlist.entity.Role;
import com.carvajal.wishlist.exception.InvalidCredentialsException;
import com.carvajal.wishlist.exception.UsernameAlreadyExistsException;
import com.carvajal.wishlist.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegister_Success() throws Exception {
        UserDTO userDTO = new UserDTO(null, "testuser", "test@example.com", "password", Role.CLIENT);
        AuthResponseDTO responseDTO = new AuthResponseDTO("fake-jwt-token", "testuser", Role.CLIENT);

        when(authService.register(any(UserDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void testRegister_UsernameAlreadyExists() throws Exception {
        UserDTO userDTO = new UserDTO(null, "testuser", "test@example.com", "password", Role.CLIENT);
        when(authService.register(any(UserDTO.class))).thenThrow(new UsernameAlreadyExistsException("Username exists"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
        AuthRequestDTO requestDTO = new AuthRequestDTO("testuser", "password");
        AuthResponseDTO responseDTO = new AuthResponseDTO("fake-jwt-token", "testuser", Role.CLIENT);

        when(authService.login(anyString(), anyString())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        AuthRequestDTO requestDTO = new AuthRequestDTO("testuser", "wrongpassword");
        when(authService.login(anyString(), anyString())).thenThrow(new InvalidCredentialsException("Invalid"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());
    }
}
