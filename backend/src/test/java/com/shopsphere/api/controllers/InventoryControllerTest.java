package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.responseDTO.InventoryResponseDTO;
import com.shopsphere.api.services.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private InventoryService inventoryService;

        @Test
        @WithMockUser
        void getInventory_Success() throws Exception {
                // Fix: Use default constructor and setters to avoid private constructor error
                InventoryResponseDTO response = new InventoryResponseDTO();
                response.setProductId("PROD-101");
                response.setQuantity(50);
                response.setReorderThreshold(10);

                when(inventoryService.getInventory("PROD-101")).thenReturn(response);

                mockMvc.perform(get("/api/inventory/PROD-101"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.productId").value("PROD-101"))
                        .andExpect(jsonPath("$.quantity").value(50));
        }
}