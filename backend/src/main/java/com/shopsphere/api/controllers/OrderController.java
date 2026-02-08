package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.OrderRequestDTO;
import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.services.OrderService;
import com.shopsphere.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.shopsphere.api.dto.requestDTO.OrderStatusUpdateRequestDTO;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found: " + email));
            log.info("Fetching orders for customer: {}", email);
            return ResponseEntity.ok(orderService.getOrdersByUserId(user.getId()));
        }

        log.info("Admin fetching all orders");
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(@PathVariable Long id,
            @RequestBody OrderStatusUpdateRequestDTO request) {
        log.info("Updating status for order ID: {} to {}", id, request.getStatus());
        return ResponseEntity.ok(orderService.updateOrderStatus(id, request.getStatus()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(@PathVariable Long id) {
        log.info("Canceling order ID: {}", id);
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }
}
