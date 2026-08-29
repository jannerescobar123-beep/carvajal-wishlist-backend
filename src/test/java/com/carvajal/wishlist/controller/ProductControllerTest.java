package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.ProductDTO;
import com.carvajal.wishlist.entity.Product;
import com.carvajal.wishlist.service.ProductService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
@Import(ProductControllerTest.TestSecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;


    private Product createProduct() {

        Product product = new Product();

        product.setId(1L);
        product.setName("Producto prueba");
        product.setDescription("Descripción de prueba");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(10);
        product.setIsActive(true);

        return product;
    }


    private ProductDTO createDto() {

        ProductDTO dto = new ProductDTO();

        dto.setName("Producto prueba");
        dto.setDescription("Descripción de prueba");
        dto.setPrice(new BigDecimal("100.00"));
        dto.setStock(10);
        dto.setIsActive(true);

        return dto;
    }


    private String toJson(Object object) throws Exception {

        return new com.fasterxml.jackson.databind.ObjectMapper()
                .writeValueAsString(object);
    }


    @Test
    void findAll_shouldReturn200() throws Exception {

        Product product = createProduct();

        when(productService.findAll())
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Producto prueba"));

        verify(productService).findAll();
    }


    @Test
    void findById_shouldReturn200() throws Exception {

        Product product = createProduct();

        when(productService.findById(1L))
                .thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Producto prueba"));

        verify(productService).findById(1L);
    }


    @Test
    void create_shouldReturn201() throws Exception {

        ProductDTO dto = createDto();
        Product product = createProduct();

        when(productService.create(any(ProductDTO.class)))
                .thenReturn(product);

        mockMvc.perform(
                        post("/api/products")
                                .contentType("application/json")
                                .content(toJson(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Producto prueba"));

        verify(productService).create(any(ProductDTO.class));
    }


    @Test
    void create_withInvalidData_shouldReturn400() throws Exception {

        ProductDTO dto = createDto();

        dto.setName("");
        dto.setPrice(new BigDecimal("-10"));
        dto.setStock(-5);

        mockMvc.perform(
                        post("/api/products")
                                .contentType("application/json")
                                .content(toJson(dto))
                )
                .andExpect(status().isBadRequest());
    }


    @Test
    void update_shouldReturn200() throws Exception {

        ProductDTO dto = createDto();
        Product product = createProduct();

        when(productService.update(
                eq(1L),
                any(ProductDTO.class)
        )).thenReturn(product);

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType("application/json")
                                .content(toJson(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Producto prueba"));

        verify(productService)
                .update(eq(1L), any(ProductDTO.class));
    }


    @Test
    void delete_shouldReturn204() throws Exception {

        doNothing()
                .when(productService)
                .delete(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService)
                .delete(1L);
    }


    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain testSecurityFilterChain(
                HttpSecurity http
        ) throws Exception {

            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth ->
                            auth.anyRequest().permitAll()
                    );

            return http.build();
        }
    }
}