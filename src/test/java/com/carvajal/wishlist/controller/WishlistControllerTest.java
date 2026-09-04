package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.WishlistDTO;
import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.entity.Role;
import com.carvajal.wishlist.entity.User;
import com.carvajal.wishlist.repository.UserRepository;
import com.carvajal.wishlist.service.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishlistService wishlistService;

    @MockitoBean
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        User user = new User("testuser", "test@example.com", "password", Role.CLIENT);
        user.setId(1L);
        
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testuser", null, List.of())
        );
    }

    @Test
    void testGetWishlist_Success() throws Exception {
        WishlistDTO wishlistDTO = new WishlistDTO(1L, "Product", 1, BigDecimal.valueOf(100), true);
        when(wishlistService.getWishlist(anyLong())).thenReturn(List.of(wishlistDTO));

        mockMvc.perform(get("/api/wishlist"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testAddToWishlist_Success() throws Exception {
        WishlistItemDTO requestDTO = new WishlistItemDTO(1L, 1);
        WishlistDTO responseDTO = new WishlistDTO(1L, "Product", 1, BigDecimal.valueOf(100), true);

        when(wishlistService.addToWishlist(anyLong(), any(WishlistItemDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/wishlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void testRemoveFromWishlist_Success() throws Exception {
        doNothing().when(wishlistService).removeFromWishlist(anyLong(), anyLong());

        mockMvc.perform(delete("/api/wishlist/1"))
                .andExpect(status().isNoContent());
    }
}
