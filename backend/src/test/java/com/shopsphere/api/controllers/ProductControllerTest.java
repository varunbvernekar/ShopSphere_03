package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.ProductRequestDTO;
import com.shopsphere.api.dto.responseDTO.ProductResponseDTO;
import com.shopsphere.api.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private com.shopsphere.api.services.InventoryService inventoryService;

    @InjectMocks
    private ProductController productController;

    private ProductRequestDTO productRequest;
    private ProductResponseDTO productResponse;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequestDTO();
        productRequest.setName("Test Product");
        productRequest.setBasePrice(100.0);
        productRequest.setIsActive(true);

        productResponse = ProductResponseDTO.builder()
                .productId("P1")
                .name("Test Product")
                .basePrice(100.0)
                .stockLevel(10)
                .build();
    }

    @Test
    void getAllProducts_ShouldReturnList() {
        when(productService.getAllProducts()).thenReturn(Collections.singletonList(productResponse));

        ResponseEntity<List<ProductResponseDTO>> response = productController.getAllProducts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Product", response.getBody().get(0).getName());
        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_ShouldReturnProduct() {
        when(productService.getProductById("P1")).thenReturn(Optional.of(productResponse));

        ResponseEntity<ProductResponseDTO> response = productController.getProductById("P1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("P1", response.getBody().getProductId());
        verify(productService).getProductById("P1");
    }

    @Test
    void updateProduct_ShouldReturnUpdatedProduct() {
        when(productService.updateProduct(eq("P1"), any(ProductRequestDTO.class))).thenReturn(productResponse);

        ResponseEntity<ProductResponseDTO> response = productController.updateProduct("P1", productRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("P1", response.getBody().getProductId());
        verify(productService).updateProduct("P1", productRequest);
    }
}
