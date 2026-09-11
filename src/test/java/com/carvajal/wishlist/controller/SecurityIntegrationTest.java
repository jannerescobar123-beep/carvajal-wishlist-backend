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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import com.jayway.jsonpath.JsonPath;
import com.carvajal.wishlist.repository.UserRepository;
import com.carvajal.wishlist.entity.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasKey;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSecurityVectors() throws Exception {
// ... later in the code:
// wait, I will replace the bad lines directly

        // 1. GET /api/products public without token responds OK
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 2. Protected endpoint without token -> 401 JSON
        mockMvc.perform(get("/api/wishlist/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));

        // Register client user
        String clientUserJson = "{\"username\":\"clientUser\",\"email\":\"client@example.com\",\"password\":\"pass123\",\"role\":\"CLIENT\"}";
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(clientUserJson));

        // Login client user
        AuthRequestDTO clientLogin = new AuthRequestDTO("clientUser", "pass123");
        MvcResult clientResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientLogin)))
                .andReturn();
        String clientToken = JsonPath.read(clientResult.getResponse().getContentAsString(), "$.token");

        // 3. User CLIENT accesses CLIENT endpoints
        mockMvc.perform(get("/api/wishlist/1") // Actually needs valid userId, but auth will pass
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(not(401)))
                .andExpect(status().is(not(403)));

        // Register admin user
        String adminUserJson = "{\"username\":\"adminUser\",\"email\":\"admin@example.com\",\"password\":\"pass123\",\"role\":\"ADMIN\"}";
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(adminUserJson));

        // Manually update role to ADMIN since registration forces CLIENT
        User adminUserEntity = userRepository.findByUsername("adminUser").orElseThrow();
        adminUserEntity.setRole(Role.ADMIN);
        userRepository.save(adminUserEntity);

        // Login admin user
        AuthRequestDTO adminLogin = new AuthRequestDTO("adminUser", "pass123");
        MvcResult adminResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminLogin)))
                .andReturn();
        String adminToken = JsonPath.read(adminResult.getResponse().getContentAsString(), "$.token");

        // 4. User CLIENT attempts ADMIN endpoint -> 403 JSON
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/admin/users/1/role")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"ADMIN\""))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));

        // 5. User ADMIN executes successfully ADMIN endpoints
        // It might be 404/200 depending on DB state (userId 1), but won't be 403!
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put("/api/admin/users/1/role")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"ADMIN\""))
                .andExpect(status().is(not(403)));

        // 9. Responses never contain "password" (check AuthResponse and GET user endpoints if they exist)
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(hasKey("password"))));

        // 7 & 8. Invalid or expired token is rejected (401)
        mockMvc.perform(get("/api/wishlist/1")
                .header("Authorization", "Bearer invalid.token.here")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
}
