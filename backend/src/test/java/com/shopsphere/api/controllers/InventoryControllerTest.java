package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.StockUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.InventoryResponseDTO;
import com.shopsphere.api.services.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private InventoryResponseDTO inventoryResponse;
    private StockUpdateRequestDTO stockUpdateRequest;

    @BeforeEach
    void setUp() {
        inventoryResponse = InventoryResponseDTO.builder()
                .productId("P1")
                .quantity(100)
                .reorderThreshold(10)
                .build();

        stockUpdateRequest = new StockUpdateRequestDTO();
        stockUpdateRequest.setQuantity(50);
        stockUpdateRequest.setThreshold(5);
    }

    @Test
    void getInventory_ShouldReturnAllInventory() {
        when(inventoryService.getAllInventory()).thenReturn(java.util.List.of(inventoryResponse));

        ResponseEntity<java.util.List<InventoryResponseDTO>> response = inventoryController.getInventory();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(inventoryService).getAllInventory();
    }

    @Test
    void updateInventory_ShouldReturnUpdatedInventory() {
        when(inventoryService.updateInventory(eq("P1"), any(StockUpdateRequestDTO.class)))
                .thenReturn(inventoryResponse);

        ResponseEntity<InventoryResponseDTO> response = inventoryController.updateInventory("P1", stockUpdateRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(inventoryService).updateInventory("P1", stockUpdateRequest);
    }
}
