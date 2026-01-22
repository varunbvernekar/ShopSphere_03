package com.shopsphere.api.services;

import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.dto.requestDTO.OrderRequestDTO;
import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO orderRequest);

    List<OrderResponseDTO> getOrdersByUserId(Long userId);

    List<OrderResponseDTO> getAllOrders();

    Optional<OrderResponseDTO> getOrderById(Long id);

    OrderResponseDTO updateOrderStatus(Long id, OrderStatus status);

    OrderResponseDTO updateOrder(Long id, OrderRequestDTO orderRequest);
}
