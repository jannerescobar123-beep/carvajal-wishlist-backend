package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.AuthRequestDTO;
import com.carvajal.wishlist.dto.ProductDTO;
import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.dto.UserDTO;
import com.carvajal.wishlist.entity.Role;
import com.carvajal.wishlist.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class WishlistIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        String userJson = "{\"username\":\"integrationUser\",\"email\":\"integration@example.com\",\"password\":\"password123\",\"role\":\"CLIENT\"}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        AuthRequestDTO loginRequest = new AuthRequestDTO("integrationUser", "password123");
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        userToken = objectMapper.readTree(loginResponse).get("token").asText();

        String adminJson = "{\"username\":\"wlAdmin\",\"email\":\"wlAdmin@test.com\",\"password\":\"password123\",\"role\":\"CLIENT\"}";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJson))
                .andExpect(status().isCreated());

        com.carvajal.wishlist.entity.User adminUser = userRepository.findByUsername("wlAdmin").orElseThrow();
        adminUser.setRole(Role.ADMIN);
        userRepository.save(adminUser);

        AuthRequestDTO adminLogin = new AuthRequestDTO("wlAdmin", "password123");
        String adminLoginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminLogin)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        adminToken = objectMapper.readTree(adminLoginResponse).get("token").asText();
    }

    private long createProduct(String name, BigDecimal price, int stock) throws Exception {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(name);
        productDTO.setDescription("Test product");
        productDTO.setPrice(price);
        productDTO.setStock(stock);
        productDTO.setIsActive(true);

        String response = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void fullWishlistFlow_success() throws Exception {
        long productId = createProduct("Laptop", BigDecimal.valueOf(999.99), 10);

        WishlistItemDTO item = new WishlistItemDTO(productId, 1);

        mockMvc.perform(post("/api/wishlist")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.inStock").value(true));

        mockMvc.perform(get("/api/wishlist")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(productId));
    }

    @Test
    void addToWishlist_duplicateProduct_throwsBadRequest() throws Exception {
        long productId = createProduct("Phone", BigDecimal.valueOf(499.99), 5);

        WishlistItemDTO item = new WishlistItemDTO(productId, 1);

        mockMvc.perform(post("/api/wishlist")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/wishlist")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isBadRequest());
    }
}
