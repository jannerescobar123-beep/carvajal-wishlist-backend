package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.entity.WishlistItem;
import com.carvajal.wishlist.service.WishlistItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistItemController.class)
@AutoConfigureMockMvc(addFilters = false)
class WishlistItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WishlistItemService wishlistItemService;

    @Test
    void findAll_shouldReturnWishlistItems() throws Exception {

        WishlistItem item = new WishlistItem();
        item.setId(1L);
        item.setName("Producto de prueba");
        item.setPrice(BigDecimal.valueOf(299.99));
        item.setPurchased(false);

        when(wishlistItemService.findAll())
                .thenReturn(List.of(item));

        mockMvc.perform(get("/api/wishlist"))
                .andExpect(status().isOk());

        verify(wishlistItemService).findAll();
    }

    @Test
    void findById_shouldReturnWishlistItem() throws Exception {

        WishlistItem item = new WishlistItem();
        item.setId(1L);
        item.setName("Producto de prueba");
        item.setPrice(BigDecimal.valueOf(299.99));
        item.setPurchased(false);

        when(wishlistItemService.findById(1L))
                .thenReturn(item);

        mockMvc.perform(get("/api/wishlist/1"))
                .andExpect(status().isOk());

        verify(wishlistItemService).findById(1L);
    }

    @Test
    void create_shouldReturnCreated() throws Exception {

        WishlistItem item = new WishlistItem();
        item.setId(1L);
        item.setName("Producto de prueba");
        item.setUrl("https://example.com");
        item.setPrice(BigDecimal.valueOf(299.99));
        item.setPurchased(false);

        when(wishlistItemService.create(any(WishlistItemDTO.class)))
                .thenReturn(item);

        String json = """
                {
                    "name": "Producto de prueba",
                    "url": "https://example.com",
                    "price": 100.00,
                    "purchased": false
                }
                """;

        mockMvc.perform(post("/api/wishlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        verify(wishlistItemService)
                .create(any(WishlistItemDTO.class));
    }

    @Test
    void update_shouldReturnOk() throws Exception {

        WishlistItem item = new WishlistItem();
        item.setId(1L);
        item.setName("Producto actualizado");
        item.setUrl("https://example.com");
        item.setPrice(BigDecimal.valueOf(299.99));
        item.setPurchased(true);

        when(wishlistItemService.update(
                eq(1L),
                any(WishlistItemDTO.class)))
                .thenReturn(item);

        String json = """
                {
                    "name": "Producto actualizado",
                    "url": "https://example.com",
                    "price": 150.00,
                    "purchased": true
                }
                """;

        mockMvc.perform(put("/api/wishlist/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(wishlistItemService)
                .update(eq(1L), any(WishlistItemDTO.class));
    }

    @Test
    void delete_shouldReturnNoContent() throws Exception {

        doNothing()
                .when(wishlistItemService)
                .delete(1L);

        mockMvc.perform(delete("/api/wishlist/1"))
                .andExpect(status().isNoContent());

        verify(wishlistItemService).delete(1L);
    }
}