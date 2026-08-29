package com.carvajal.wishlist.service;

import com.carvajal.wishlist.dto.ProductDTO;
import com.carvajal.wishlist.entity.Product;
import com.carvajal.wishlist.exception.ResourceNotFoundException;
import com.carvajal.wishlist.exception.StockNotAvailableException;
import com.carvajal.wishlist.repository.ProductRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductDTO dto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Producto prueba");
        product.setDescription("Descripción");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(10);
        product.setIsActive(true);

        dto = new ProductDTO();
        dto.setName("Producto prueba");
        dto.setDescription("Descripción");
        dto.setPrice(new BigDecimal("100.00"));
        dto.setStock(10);
        dto.setIsActive(true);
    }

    @Test
    void findAll_shouldReturnProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals(product, result.get(0));
        verify(productRepository).findAll();
    }

    @Test
    void findById_shouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.findById(1L);

        assertEquals(product, result);
        verify(productRepository).findById(1L);
    }

    @Test
    void findById_shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(99L)
        );

        verify(productRepository).findById(99L);
    }

    @Test
    void create_shouldSaveProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.create(dto);

        assertEquals(product, result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_shouldUpdateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product result = productService.update(1L, dto);

        assertEquals("Producto prueba", result.getName());
        assertEquals(new BigDecimal("100.00"), result.getPrice());
        assertEquals(10, result.getStock());

        verify(productRepository).findById(1L);
        verify(productRepository).save(product);
    }

    @Test
    void delete_shouldDeleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void hasStock_shouldReturnTrueWhenStockIsAvailable() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertTrue(productService.hasStock(1L, 5));
    }

    @Test
    void hasStock_shouldThrowWhenStockIsNotAvailable() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(
                StockNotAvailableException.class,
                () -> productService.hasStock(1L, 11)
        );
    }
}