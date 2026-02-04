package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.LogisticsInfoRequestDTO;

import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;
import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.services.DeliveryService;
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
class DeliveryControllerTest {

    @Mock
    private DeliveryService deliveryService;

    @InjectMocks
    private DeliveryController deliveryController;

    private OrderResponseDTO orderResponse;
    private LogisticsInfoRequestDTO logisticsRequest;

    @BeforeEach
    void setUp() {
        orderResponse = OrderResponseDTO.builder()
                .id(1L)
                .status(OrderStatus.Shipped)
                .build();

        logisticsRequest = new LogisticsInfoRequestDTO();
        logisticsRequest.setCarrier("DHL");
        logisticsRequest.setTrackingId("12345"); // Changed from setTrackingNumber

    }

    @Test
    void updateLogistics_ShouldReturnUpdatedOrder() {
        when(deliveryService.updateLogistics(eq(1L), any(LogisticsInfoRequestDTO.class))).thenReturn(orderResponse);

        ResponseEntity<OrderResponseDTO> response = deliveryController.updateLogistics(1L, logisticsRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(OrderStatus.Shipped, response.getBody().getStatus());
        verify(deliveryService).updateLogistics(1L, logisticsRequest);
    }
}
