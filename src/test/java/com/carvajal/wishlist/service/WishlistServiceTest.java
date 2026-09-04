package com.carvajal.wishlist.service;

import com.carvajal.wishlist.dto.WishlistDTO;
import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.entity.Product;
import com.carvajal.wishlist.entity.Role;
import com.carvajal.wishlist.entity.User;
import com.carvajal.wishlist.entity.Wishlist;
import com.carvajal.wishlist.exception.ProductAlreadyInWishlistException;
import com.carvajal.wishlist.exception.ResourceNotFoundException;
import com.carvajal.wishlist.exception.StockNotAvailableException;
import com.carvajal.wishlist.repository.ProductRepository;
import com.carvajal.wishlist.repository.UserRepository;
import com.carvajal.wishlist.repository.WishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private User user;
    private Product product;
    private WishlistItemDTO wishlistItemDTO;
    private Wishlist wishlist;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "test@example.com", "password", Role.CLIENT);
        user.setId(1L);
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(100));
        product.setStock(10);
        product.setIsActive(true);

        wishlistItemDTO = new WishlistItemDTO(1L, 2);
        wishlist = new Wishlist(user, product, 2);
    }

    @Test
    void testAddToWishlist_Success() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(wishlistRepository.findByUserIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(productService.hasStock(anyLong(), anyInt())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(wishlist);

        WishlistDTO response = wishlistService.addToWishlist(1L, wishlistItemDTO);

        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        verify(wishlistRepository, times(1)).save(any(Wishlist.class));
    }

    @Test
    void testAddToWishlist_ProductAlreadyInWishlist() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(wishlistRepository.findByUserIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(wishlist));

        assertThrows(ProductAlreadyInWishlistException.class, () -> wishlistService.addToWishlist(1L, wishlistItemDTO));
    }

    @Test
    void testAddToWishlist_StockNotAvailable() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(wishlistRepository.findByUserIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(productService.hasStock(anyLong(), anyInt())).thenThrow(new StockNotAvailableException("No stock"));

        assertThrows(StockNotAvailableException.class, () -> wishlistService.addToWishlist(1L, wishlistItemDTO));
    }

    @Test
    void testRemoveFromWishlist_Success() {
        when(wishlistRepository.findByUserIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(wishlist));
        doNothing().when(wishlistRepository).deleteByUserIdAndProductId(anyLong(), anyLong());

        wishlistService.removeFromWishlist(1L, 1L);

        verify(wishlistRepository, times(1)).deleteByUserIdAndProductId(1L, 1L);
    }

    @Test
    void testGetWishlist_Success() {
        when(wishlistRepository.findByUserId(anyLong())).thenReturn(List.of(wishlist));
        when(productService.hasStock(anyLong(), anyInt())).thenReturn(true);

        List<WishlistDTO> response = wishlistService.getWishlist(1L);

        assertFalse(response.isEmpty());
        assertTrue(response.get(0).getInStock());
    }

    @Test
    void testGetWishlistHistory_Success() {
        when(wishlistRepository.findAllByUserIdOrderByCreatedAtDesc(anyLong())).thenReturn(List.of(wishlist));
        when(productService.hasStock(anyLong(), anyInt())).thenReturn(true);

        List<WishlistDTO> response = wishlistService.getWishlistHistory(1L);

        assertFalse(response.isEmpty());
    }
}
