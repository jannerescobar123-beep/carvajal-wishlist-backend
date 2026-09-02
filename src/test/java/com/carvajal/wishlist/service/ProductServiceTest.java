package com.carvajal.wishlist.service;

import com.carvajal.wishlist.dto.ProductDTO;
import com.carvajal.wishlist.entity.Product;
import com.carvajal.wishlist.exception.ResourceNotFoundException;
import com.carvajal.wishlist.exception.StockNotAvailableException;
import com.carvajal.wishlist.repository.ProductRepository;
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

    @Test
    void findAll_shouldReturnActiveProducts() {
        Product product = createProduct(1L, "Laptop", true);

        when(productRepository.findAllByIsActiveTrue())
                .thenReturn(List.of(product));

        List<ProductDTO> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertTrue(result.get(0).getIsActive());

        verify(productRepository).findAllByIsActiveTrue();
    }

    @Test
    void findById_shouldReturnActiveProduct() {
        Product product = createProduct(1L, "Laptop", true);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        ProductDTO result = productService.findById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
    }

    @Test
    void findById_shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(99L)
        );
    }

    @Test
    void findById_shouldThrowWhenProductIsInactive() {
        Product product = createProduct(1L, "Laptop", false);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(1L)
        );
    }

    @Test
    void create_shouldSaveProduct() {
        ProductDTO dto = createDTO("Laptop", true);

        Product savedProduct = createProduct(1L, "Laptop", true);

        when(productRepository.save(any(Product.class)))
                .thenReturn(savedProduct);

        ProductDTO result = productService.create(dto);

        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        assertEquals(BigDecimal.valueOf(999.99), result.getPrice());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void update_shouldUpdateProduct() {
        Product product = createProduct(1L, "Laptop", true);

        ProductDTO dto = createDTO("Updated Laptop", true);
        dto.setPrice(BigDecimal.valueOf(1299.99));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productRepository.save(product))
                .thenReturn(product);

        ProductDTO result = productService.update(1L, dto);

        assertEquals("Updated Laptop", result.getName());
        assertEquals(BigDecimal.valueOf(1299.99), result.getPrice());

        verify(productRepository).save(product);
    }

    @Test
    void update_shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        ProductDTO dto = createDTO("Laptop", true);

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.update(99L, dto)
        );

        verify(productRepository, never()).save(any());
    }

    @Test
    void delete_shouldDeleteProduct() {
        Product product = createProduct(1L, "Laptop", true);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        productService.delete(1L);

        verify(productRepository).delete(product);
    }

    @Test
    void delete_shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.delete(99L)
        );

        verify(productRepository, never()).delete(any());
    }

    @Test
    void hasStock_shouldReturnTrueWhenEnoughStockExists() {
        Product product = createProduct(1L, "Laptop", true);
        product.setStock(10);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertTrue(productService.hasStock(1L, 5));
    }

    @Test
    void hasStock_shouldThrowWhenStockIsInsufficient() {
        Product product = createProduct(1L, "Laptop", true);
        product.setStock(2);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        assertThrows(
                StockNotAvailableException.class,
                () -> productService.hasStock(1L, 5)
        );
    }

    @Test
    void hasStock_shouldThrowWhenProductDoesNotExist() {
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.hasStock(99L, 1)
        );
    }

    private Product createProduct(Long id, String name, Boolean active) {
        Product product = new Product();

        product.setId(id);
        product.setName(name);
        product.setDescription("Test product");
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setStock(10);
        product.setIsActive(active);

        return product;
    }

    private ProductDTO createDTO(String name, Boolean active) {
        ProductDTO dto = new ProductDTO();

        dto.setName(name);
        dto.setDescription("Test product");
        dto.setPrice(BigDecimal.valueOf(999.99));
        dto.setStock(10);
        dto.setIsActive(active);

        return dto;
    }
}