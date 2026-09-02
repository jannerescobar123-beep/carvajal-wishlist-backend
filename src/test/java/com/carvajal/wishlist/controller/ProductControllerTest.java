package com.carvajal.wishlist.controller;

import com.carvajal.wishlist.dto.ProductDTO;
import com.carvajal.wishlist.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import com.carvajal.wishlist.config.SecurityConfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void findAll_shouldReturnProducts() throws Exception {

        ProductDTO product = createProduct(1L, "Laptop");

        when(productService.findAll())
                .thenReturn(List.of(product));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(
                        content().contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        )
                )
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }


    @Test
    void findById_shouldReturnProduct() throws Exception {

        ProductDTO product = createProduct(1L, "Laptop");

        when(productService.findById(1L))
                .thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldReturnCreatedProduct() throws Exception {

        ProductDTO request = createProduct(null, "Laptop");
        ProductDTO response = createProduct(1L, "Laptop");

        when(productService.create(any(ProductDTO.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void update_shouldReturnUpdatedProduct() throws Exception {

        ProductDTO request =
                createProduct(null, "Laptop Updated");

        ProductDTO response =
                createProduct(1L, "Laptop Updated");

        when(productService.update(
                eq(1L),
                any(ProductDTO.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.name")
                                .value("Laptop Updated")
                );
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturnNoContent() throws Exception {

        doNothing()
                .when(productService)
                .delete(1L);

        mockMvc.perform(
                        delete("/api/products/1")
                )
                .andExpect(status().isNoContent());
    }


    /*
     * Usuario autenticado,
     * pero SIN rol ADMIN.
     *
     * Debe devolver 403.
     */
    @Test
    @WithMockUser(roles = "USER")
    void create_withoutAdminRole_shouldReturnForbidden()
            throws Exception {

        ProductDTO request =
                createProduct(null, "Laptop");

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "USER")
    void update_withoutAdminRole_shouldReturnForbidden()
            throws Exception {

        ProductDTO request =
                createProduct(null, "Laptop");

        mockMvc.perform(
                        put("/api/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "USER")
    void delete_withoutAdminRole_shouldReturnForbidden()
            throws Exception {

        mockMvc.perform(
                        delete("/api/products/1")
                )
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withBlankName_shouldReturnBadRequest()
            throws Exception {

        ProductDTO request =
                createProduct(null, "");

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isBadRequest());
    }


    private ProductDTO createProduct(
            Long id,
            String name
    ) {

        ProductDTO product = new ProductDTO();

        product.setId(id);
        product.setName(name);
        product.setDescription("Test product");
        product.setPrice(new BigDecimal("99.99"));
        product.setStock(10);
        product.setIsActive(true);

        return product;
    }

    @Test
    void hasStock_shouldReturnTrueWhenStockIsAvailable()
            throws Exception {

        when(productService.hasStock(1L, 3))
                .thenReturn(true);

        mockMvc.perform(
                        get("/api/products/1/stock")
                                .param("quantity", "3")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}