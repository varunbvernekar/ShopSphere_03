package com.shopsphere.api.services.impl;

import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.dto.requestDTO.OrderRequestDTO;
import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;
import com.shopsphere.api.entity.Order;
import com.shopsphere.api.repositories.OrderRepository;
import com.shopsphere.api.services.InventoryService;
import com.shopsphere.api.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.shopsphere.api.entity.Cart;
import com.shopsphere.api.entity.LogisticsInfo;
import com.shopsphere.api.entity.OrderItem;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.repositories.CartRepository;
import com.shopsphere.api.repositories.UserRepository;
import com.shopsphere.api.services.CartService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        Long userId = orderRequest.getUserId();
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with empty cart");
        }

        // Create Order from Cart
        Order.OrderBuilder orderBuilder = Order.builder()
                .userId(userId)
                .placedOn(LocalDateTime.now())
                .status(OrderStatus.Placed) // Default status
                .estimatedDelivery(orderRequest.getEstimatedDelivery())
                .deliveryAddress(orderRequest.getDeliveryAddress());

        // Map Cart Items to Order Items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .productId(cartItem.getProductId())
                        .name(cartItem.getName())
                        .image(cartItem.getImage())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .color(cartItem.getColor())
                        .size(cartItem.getSize())
                        .material(cartItem.getMaterial())
                        .build())
                .collect(Collectors.toList());

        orderBuilder.items(orderItems);

        // Core Business Logic: Calculate Total on Backend
        double subtotal = orderItems.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        double tax = subtotal * 0.10;
        double shipping = subtotal > 0 ? 50.0 : 0.0;
        double totalAmount = subtotal + tax + shipping;

        orderBuilder.amount(totalAmount);

        // Logistics (if provided, though typically empty at creation)
        if (orderRequest.getLogistics() != null) {
            orderBuilder.logistics(LogisticsInfo.builder()
                    .carrier(orderRequest.getLogistics().getCarrier())
                    .trackingId(orderRequest.getLogistics().getTrackingId())
                    .currentLocation(orderRequest.getLogistics().getCurrentLocation())
                    .build());
        }

        Order order = orderBuilder.build();

        // Validate and update stock
        for (var item : order.getItems()) {
            inventoryService.reduceStock(item.getProductId(), item.getQuantity());
        }

        Order savedOrder = orderRepository.save(order);

        // Clear Cart
        cartService.clearCart(userId);

        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (status == OrderStatus.Cancelled) {
            throw new RuntimeException("Use cancelOrder to cancel orders.");
        }

        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        // Security Check
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // CUSTOMER RULES
            String email = auth.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!order.getUserId().equals(user.getId())) {
                throw new RuntimeException("Access Denied: You do not own this order.");
            }
        }

        // Cancellation Rules (Same for Admin & Customer for now, based on state)
        if (currentStatus == OrderStatus.Shipped || currentStatus == OrderStatus.Delivered) {
            throw new RuntimeException("Cannot cancel order that has already been shipped or delivered.");
        }

        if (currentStatus == OrderStatus.Cancelled) {
            throw new RuntimeException("Order is already cancelled.");
        }

        // Logic for Cancellation (Restore Stock)
        if (order.getItems() != null) {
            for (var item : order.getItems()) {
                if (item.getProductId() != null) {
                    try {
                        inventoryService.increaseStock(item.getProductId(), item.getQuantity());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to restore stock for product: " + item.getProductId(), e);
                    }
                }
            }
        }

        order.setStatus(OrderStatus.Cancelled);
        Order savedOrder = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(savedOrder);
    }
}
