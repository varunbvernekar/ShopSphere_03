package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.StockUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.InventoryResponseDTO;
import com.shopsphere.api.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@lombok.extern.slf4j.Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getInventory() {
        log.info("Fetching all inventory");
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> updateInventory(@PathVariable String productId,
            @RequestBody StockUpdateRequestDTO request) {
        log.info("Updating inventory for product ID: {}", productId);
        return ResponseEntity.ok(inventoryService.updateInventory(productId, request));
    }
}
