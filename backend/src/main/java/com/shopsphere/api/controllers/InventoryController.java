package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.StockUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.InventoryResponseDTO;
import com.shopsphere.api.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@lombok.extern.slf4j.Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> getInventory(@PathVariable String productId) {
        log.debug("Fetching inventory for product ID: {}", productId);
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> updateInventory(@PathVariable String productId,
            @RequestBody StockUpdateRequestDTO request) {
        log.info("Updating inventory for product ID: {}", productId);
        return ResponseEntity.ok(inventoryService.updateInventory(productId, request));
    }
}
