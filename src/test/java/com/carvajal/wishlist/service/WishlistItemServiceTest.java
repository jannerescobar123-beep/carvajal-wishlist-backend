package com.carvajal.wishlist.service;

import com.carvajal.wishlist.dto.WishlistItemDTO;
import com.carvajal.wishlist.entity.WishlistItem;
import com.carvajal.wishlist.exception.ResourceNotFoundException;
import com.carvajal.wishlist.repository.WishlistItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WishlistItemServiceTest {

    @Mock
    private WishlistItemRepository wishlistItemRepository;

    @InjectMocks
    private WishlistItemService wishlistItemService;

    @Test
    void findAll_shouldReturnWishlistItems() {
        WishlistItem item = new WishlistItem();
        item.setId(1L);
        item.setName("Producto de prueba");

        List<WishlistItem> items = List.of(item);

        when(wishlistItemRepository.findAll()).thenReturn(items);

        List<WishlistItem> result = wishlistItemService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Producto de prueba", result.get(0).getName());

        verify(wishlistItemRepository).findAll();
    }

    @Test
    void findById_shouldReturnWishlistItem() {
        WishlistItem item = new WishlistItem();
        item.setId(1L);
        item.setName("Producto de prueba");

        when(wishlistItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        WishlistItem result = wishlistItemService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Producto de prueba", result.getName());

        verify(wishlistItemRepository).findById(1L);
    }

    @Test
    void findById_whenNotFound_shouldThrowException() {
        when(wishlistItemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> wishlistItemService.findById(1L)
        );

        verify(wishlistItemRepository).findById(1L);
    }

    @Test
    void create_shouldSaveWishlistItem() {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setName("Producto de prueba");
        dto.setUrl("https://example.com");
        dto.setPrice(99.99);
        dto.setPurchased(true);

        WishlistItem savedItem = new WishlistItem();
        savedItem.setId(1L);
        savedItem.setName("Producto de prueba");
        savedItem.setUrl("https://example.com");
        savedItem.setPrice(BigDecimal.valueOf(99.99));
        savedItem.setPurchased(true);

        when(wishlistItemRepository.save(any(WishlistItem.class)))
                .thenReturn(savedItem);

        WishlistItem result = wishlistItemService.create(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Producto de prueba", result.getName());
        assertEquals("https://example.com", result.getUrl());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        assertTrue(result.getPurchased());

        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    void create_whenPurchasedIsNull_shouldSetFalse() {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setName("Producto de prueba");
        dto.setPrice(50.0);
        dto.setPurchased(null);

        when(wishlistItemRepository.save(any(WishlistItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WishlistItem result = wishlistItemService.create(dto);

        assertNotNull(result);
        assertEquals("Producto de prueba", result.getName());
        assertEquals(BigDecimal.valueOf(50.0), result.getPrice());
        assertFalse(result.getPurchased());

        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    void update_shouldUpdateWishlistItem() {
        WishlistItem existingItem = new WishlistItem();
        existingItem.setId(1L);
        existingItem.setName("Producto anterior");
        existingItem.setUrl("https://old.com");
        existingItem.setPrice(BigDecimal.valueOf(299.99));
        existingItem.setPurchased(false);

        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setName("Producto actualizado");
        dto.setUrl("https://new.com");
        dto.setPrice(100.0);
        dto.setPurchased(true);

        when(wishlistItemRepository.findById(1L))
                .thenReturn(Optional.of(existingItem));

        when(wishlistItemRepository.save(any(WishlistItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WishlistItem result = wishlistItemService.update(1L, dto);

        assertNotNull(result);
        assertEquals("Producto actualizado", result.getName());
        assertEquals("https://new.com", result.getUrl());
        assertEquals(BigDecimal.valueOf(100.0), result.getPrice());
        assertTrue(result.getPurchased());

        verify(wishlistItemRepository).findById(1L);
        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    void update_whenPurchasedIsNull_shouldKeepPreviousValue() {
        WishlistItem existingItem = new WishlistItem();
        existingItem.setId(1L);
        existingItem.setName("Producto anterior");
        existingItem.setPrice(BigDecimal.valueOf(299.99));
        existingItem.setPurchased(true);

        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setName("Producto actualizado");
        dto.setPrice(75.0);
        dto.setPurchased(null);

        when(wishlistItemRepository.findById(1L))
                .thenReturn(Optional.of(existingItem));

        when(wishlistItemRepository.save(any(WishlistItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WishlistItem result = wishlistItemService.update(1L, dto);

        assertEquals("Producto actualizado", result.getName());
        assertEquals(BigDecimal.valueOf(75.0), result.getPrice());
        assertTrue(result.getPurchased());

        verify(wishlistItemRepository).findById(1L);
        verify(wishlistItemRepository).save(any(WishlistItem.class));
    }

    @Test
    void update_whenNotFound_shouldThrowException() {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setName("Producto actualizado");
        dto.setPrice(100.0);

        when(wishlistItemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> wishlistItemService.update(1L, dto)
        );

        verify(wishlistItemRepository).findById(1L);
        verify(wishlistItemRepository, never())
                .save(any(WishlistItem.class));
    }

    @Test
    void delete_shouldDeleteWishlistItem() {
        WishlistItem item = new WishlistItem();
        item.setId(1L);
        item.setName("Producto de prueba");

        when(wishlistItemRepository.findById(1L))
                .thenReturn(Optional.of(item));

        wishlistItemService.delete(1L);

        verify(wishlistItemRepository).findById(1L);
        verify(wishlistItemRepository).delete(item);
    }

    @Test
    void delete_whenNotFound_shouldThrowException() {
        when(wishlistItemRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> wishlistItemService.delete(1L)
        );

        verify(wishlistItemRepository).findById(1L);
        verify(wishlistItemRepository, never())
                .delete(any(WishlistItem.class));
    }
}