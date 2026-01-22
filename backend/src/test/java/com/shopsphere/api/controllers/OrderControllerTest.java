package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;
import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.services.OrderService;
import com.shopsphere.api.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private OrderService orderService;

        @MockBean
        private UserService userService;

        @Test
        @WithMockUser(roles = "ADMIN")
        void updateOrderStatus_Success() throws Exception {
                // Fix: Use .id() instead of .orderId() and explicit OrderStatus.SHIPPED
                OrderResponseDTO response = OrderResponseDTO.builder()
                        .id(1L)
                        .status(OrderStatus.SHIPPED)
                        .build();

                when(orderService.updateOrderStatus(anyLong(), any(OrderStatus.class))).thenReturn(response);

                mockMvc.perform(put("/api/orders/1/status")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\": \"SHIPPED\"}"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("SHIPPED"));
        }
}