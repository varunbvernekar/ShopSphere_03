package com.shopsphere.api.controllers;

import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.dto.requestDTO.OrderRequestDTO;
import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.services.OrderService;
import com.shopsphere.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@lombok.extern.slf4j.Slf4j
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestBody OrderRequestDTO orderRequest) {
        log.info("Received create order request");
        return ResponseEntity.ok(orderService.createOrder(orderRequest));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders(@RequestParam(required = false) Long userId) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            log.info("Fetching orders for customer: {}", email);
            return ResponseEntity.ok(orderService.getOrdersByUserId(user.getId()));
        }

        if (userId != null) {
            log.info("Admin fetching orders for specific user ID: {}", userId);
            return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
        }
        log.info("Admin fetching all orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long id, @RequestBody OrderRequestDTO orderRequest) {
        log.info("Admin updating order ID: {}", id);
        return ResponseEntity.ok(orderService.updateOrder(id, orderRequest));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable Long id,
            @RequestBody com.shopsphere.api.dto.requestDTO.OrderStatusUpdateRequestDTO request) {
        log.info("Updating status for order ID: {} to {}", id, request.getStatus());
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.getStatus()));
    }
}
