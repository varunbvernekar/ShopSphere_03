package com.shopsphere.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.api.dto.responseDTO.ProductResponseDTO;
import com.shopsphere.api.services.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private ProductService productService;
    @MockBean private FileStorageService fileStorageService;
    @MockBean private InventoryService inventoryService;
    @MockBean private ObjectMapper objectMapper;

    @Test
    void getProductById_Success() throws Exception {
        ProductResponseDTO product = new ProductResponseDTO();
        product.setProductId("PROD1");

        when(productService.getProductById("PROD1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/PROD1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("PROD1"));
    }
}