package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.OrderRequestDTO;
import com.shopsphere.api.dto.requestDTO.OrderStatusUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;
import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.services.OrderService;
import com.shopsphere.api.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private OrderController orderController;

    private OrderRequestDTO orderRequest;
    private OrderResponseDTO orderResponse;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequestDTO();
        orderRequest.setUserId(1L);

        orderResponse = OrderResponseDTO.builder()
                .id(1L)
                .userId(1L)
                .status(OrderStatus.Placed)
                .amount(100.0)
                .build();
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() {
        when(orderService.createOrder(any(OrderRequestDTO.class))).thenReturn(orderResponse);

        ResponseEntity<OrderResponseDTO> response = orderController.createOrder(orderRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals(OrderStatus.Placed, response.getBody().getStatus());
        verify(orderService).createOrder(orderRequest);
    }

    @Test
    void getOrders_AsAdmin_ShouldReturnAllOrders() {
        // Setup Security Context Mocking for Admin
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(authentication).getAuthorities();

        when(orderService.getAllOrders()).thenReturn(Collections.singletonList(orderResponse));

        ResponseEntity<List<OrderResponseDTO>> response = orderController.getOrders(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(orderService).getAllOrders();

        // Cleanup
        SecurityContextHolder.clearContext();
    }

    @Test
    void updateOrderStatus_ShouldReturnUpdatedOrder() {
        orderResponse.setStatus(OrderStatus.Confirmed);
        when(orderService.updateOrderStatus(eq(1L), eq(OrderStatus.Confirmed))).thenReturn(orderResponse);

        OrderStatusUpdateRequestDTO statusRequest = new OrderStatusUpdateRequestDTO();
        statusRequest.setStatus(OrderStatus.Confirmed);

        ResponseEntity<OrderResponseDTO> response = orderController.updateOrderStatus(1L, statusRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.Confirmed, response.getBody().getStatus());
        verify(orderService).updateOrderStatus(1L, OrderStatus.Confirmed);
    }

    @Test
    void cancelOrder_ShouldReturnCancelledOrder() {
        orderResponse.setStatus(OrderStatus.Cancelled);
        when(orderService.cancelOrder(1L)).thenReturn(orderResponse);

        ResponseEntity<OrderResponseDTO> response = orderController.cancelOrder(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(OrderStatus.Cancelled, response.getBody().getStatus());
        verify(orderService).cancelOrder(1L);
    }
}
