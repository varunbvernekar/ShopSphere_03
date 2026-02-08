package com.shopsphere.api.services.impl;

import com.shopsphere.api.dto.requestDTO.StockUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.InventoryResponseDTO;
import com.shopsphere.api.entity.Inventory;
import com.shopsphere.api.repositories.InventoryRepository;
import com.shopsphere.api.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shopsphere.api.repositories.ProductRepository;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Override
    public InventoryResponseDTO getInventory(String productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .quantity(0)
                        .reorderThreshold(0)
                        .build());
        return mapToResponse(inventory);
    }

    @Override
    public java.util.List<InventoryResponseDTO> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryResponseDTO updateInventory(String productId, StockUpdateRequestDTO request) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .quantity(0)
                        .reorderThreshold(0)
                        .build());

        if (request.getQuantity() != null) {
            if (request.getQuantity() < 0) {
                throw new IllegalArgumentException("Stock cannot be negative");
            }
            inventory.setQuantity(request.getQuantity());
        }
        if (request.getThreshold() != null) {
            inventory.setReorderThreshold(request.getThreshold());
        }

        return mapToResponse(inventoryRepository.save(inventory));
    }

    @Override
    @Transactional
    public void reduceStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));

        int newQuantity = inventory.getQuantity() - quantity;
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock for product: " + productId);
        }
        inventory.setQuantity(newQuantity);
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void increaseStock(String productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void initializeInventory(String productId, Integer quantity, Integer threshold) {
        if (inventoryRepository.findByProductId(productId).isPresent()) {
            return; // Already exists
        }
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(quantity != null ? quantity : 0)
                .reorderThreshold(threshold != null ? threshold : 10)
                .build();
        inventoryRepository.save(inventory);
    }

    @Override
    @Transactional
    public void deleteInventory(String productId) {

        inventoryRepository.deleteByProductId(productId);
    }

    private InventoryResponseDTO mapToResponse(Inventory inventory) {
        String productName = productRepository.findById(inventory.getProductId())
                .map(com.shopsphere.api.entity.Product::getName)
                .orElse("Unknown Product");
        return InventoryResponseDTO.fromEntity(inventory, productName);
    }
}
