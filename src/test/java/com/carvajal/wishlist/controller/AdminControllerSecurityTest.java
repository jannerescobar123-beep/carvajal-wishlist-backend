package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.UserDTO;
import com.carvajal.wishlist.entity.Role;
import com.carvajal.wishlist.security.CustomUserDetailsService;
import com.carvajal.wishlist.security.JwtAuthenticationFilter;
import com.carvajal.wishlist.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({com.carvajal.wishlist.config.SecurityConfig.class, JwtAuthenticationFilter.class,
        TestAccessDeniedExceptionHandler.class})
class AdminControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private com.carvajal.wishlist.config.JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void updateUserRole_shouldReturnOkForAdmin() throws Exception {
        UserDTO responseDTO = new UserDTO(1L, "testuser", "test@example.com", null, Role.ADMIN);
        when(userService.updateUserRole(eq(1L), eq(Role.ADMIN))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/admin/users/1/role")
                        .with(SecurityMockMvcRequestPostProcessors.user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Role.ADMIN)))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserRole_shouldReturnForbiddenForClient() throws Exception {
        mockMvc.perform(put("/api/admin/users/1/role")
                        .with(SecurityMockMvcRequestPostProcessors.user("client").roles("CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Role.ADMIN)))
                .andExpect(status().isForbidden());
    }
}
