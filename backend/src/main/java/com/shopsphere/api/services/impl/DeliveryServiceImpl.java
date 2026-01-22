package com.shopsphere.api.services.impl;

import com.shopsphere.api.dto.requestDTO.LogisticsInfoRequestDTO;
import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;
import com.shopsphere.api.entity.LogisticsInfo;
import com.shopsphere.api.entity.Order;
import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.repositories.OrderRepository;
import com.shopsphere.api.services.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderResponseDTO updateLogistics(Long orderId, LogisticsInfoRequestDTO request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getLogistics() == null) {
            order.setLogistics(new LogisticsInfo());
        }

        order.getLogistics().setCarrier(request.getCarrier());
        order.getLogistics().setTrackingId(request.getTrackingId());
        order.getLogistics().setCurrentLocation(request.getCurrentLocation());

        // Automatically move to Shipped if tracking info is added and status is still
        // Placed/Processing
        if (order.getStatus() == OrderStatus.Placed || order.getStatus() == OrderStatus.Processing) {
            order.setStatus(OrderStatus.Shipped);
        }

        Order savedOrder = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Only allow delivery-related status updates (e.g., Shipped, Delivered)
        // Cancellation is handled by OrderService (Customer/Admin)
        if (status == OrderStatus.Cancelled) {
            throw new RuntimeException("Use OrderService to cancel orders.");
        }

        order.setStatus(status);

        Order savedOrder = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(savedOrder);
    }
}
