package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.LogisticsInfoRequestDTO;
import com.shopsphere.api.dto.requestDTO.OrderStatusUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;
import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.services.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@lombok.extern.slf4j.Slf4j
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping("/{orderId}/logistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateLogistics(@PathVariable Long orderId,
            @RequestBody LogisticsInfoRequestDTO request) {
        log.info("Updating logistics for order ID: {}", orderId);
        return ResponseEntity.ok(deliveryService.updateLogistics(orderId, request));
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponseDTO> updateDeliveryStatus(@PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequestDTO request) {
        log.info("Updating delivery status for order ID: {} to {}", orderId, request.getStatus());
        return ResponseEntity.ok(deliveryService.updateStatus(orderId, request.getStatus()));
    }
}
