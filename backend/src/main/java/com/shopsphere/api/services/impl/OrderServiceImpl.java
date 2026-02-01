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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final com.shopsphere.api.repositories.CartRepository cartRepository;
    private final com.shopsphere.api.repositories.UserRepository userRepository;
    private final com.shopsphere.api.services.CartService cartService;

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequest) {
        Long userId = orderRequest.getUserId();
        com.shopsphere.api.entity.Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order with empty cart");
        }

        // Create Order from Cart
        Order.OrderBuilder orderBuilder = Order.builder()
                .userId(userId)
                .placedOn(java.time.LocalDateTime.now())
                .status(OrderStatus.Placed) // Default status
                .estimatedDelivery(orderRequest.getEstimatedDelivery())
                .deliveryAddress(orderRequest.getDeliveryAddress());

        // Map Cart Items to Order Items
        List<com.shopsphere.api.entity.OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> com.shopsphere.api.entity.OrderItem.builder()
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
            orderBuilder.logistics(com.shopsphere.api.entity.LogisticsInfo.builder()
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
    @Transactional(readOnly = true)
    public Optional<OrderResponseDTO> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponseDTO::fromEntity);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus currentStatus = order.getStatus();

        // Security Check
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            // CUSTOMER RULES
            String email = auth.getName();
            com.shopsphere.api.entity.User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!order.getUserId().equals(user.getId())) {
                throw new RuntimeException("Access Denied: You do not own this order.");
            }

            if (status != OrderStatus.Cancelled) {
                throw new RuntimeException("Customers can only cancel orders.");
            }

            if (currentStatus != OrderStatus.Placed && currentStatus != OrderStatus.Confirmed
                    && currentStatus != OrderStatus.Packed) {
                throw new RuntimeException("You cannot cancel an order that has already been shipped or delivered.");
            }
        } else {
            // ADMIN RULES
            if (status == OrderStatus.Cancelled) {
                if (currentStatus == OrderStatus.Shipped || currentStatus == OrderStatus.Delivered) {
                    throw new RuntimeException("Cannot cancel order that has already been shipped or delivered.");
                }
            } else {
                // Enforce Valid Transitions
                boolean isValid = false;
                switch (currentStatus) {
                    case Placed:
                        if (status == OrderStatus.Confirmed)
                            isValid = true;
                        break;
                    case Confirmed:
                        if (status == OrderStatus.Packed)
                            isValid = true;
                        break;
                    case Packed:
                        if (status == OrderStatus.Shipped)
                            isValid = true;
                        break;
                    case Shipped:
                        if (status == OrderStatus.Delivered)
                            isValid = true;
                        break;
                    default:
                        break;
                }
                if (!isValid) {
                    throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + status);
                }
            }
        }

        // Logic for Cancellation (Restore Stock)
        if (status == OrderStatus.Cancelled && currentStatus != OrderStatus.Cancelled) {
            if (order.getItems() != null) {
                for (var item : order.getItems()) {
                    if (item.getProductId() != null) {
                        try {
                            inventoryService.increaseStock(item.getProductId(), item.getQuantity());
                        } catch (Exception e) {
                            // Log error but generally proceed or throw?
                            // Safer to throw to ensure transaction rollback if stock can't be restored
                            throw new RuntimeException("Failed to restore stock for product: " + item.getProductId(),
                                    e);
                        }
                    }
                }
            }
        }

        order.setStatus(status);
        Order savedOrder = orderRepository.save(order);
        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDTO updateOrder(Long id, OrderRequestDTO orderRequest) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        existing.setDeliveryAddress(orderRequest.getDeliveryAddress());
        existing.setEstimatedDelivery(orderRequest.getEstimatedDelivery());

        // Note: Status updates should be done via updateOrderStatus or separate
        // DeliveryService for logistics
        // Keeping basic status update here for now if admin wants to force update
        // without delivery logic
        if (orderRequest.getStatus() != null) {
            existing.setStatus(orderRequest.getStatus());
        }

        if (orderRequest.getItems() != null) {
            Order tempOrder = OrderRequestDTO.toEntity(orderRequest);
            existing.getItems().clear();
            existing.getItems().addAll(tempOrder.getItems());
        }

        // REMOVED: Logistics update logic. Use DeliveryService for this.

        Order savedOrder = orderRepository.save(existing);
        return OrderResponseDTO.fromEntity(savedOrder);
    }
}
