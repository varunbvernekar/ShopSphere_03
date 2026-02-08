package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.StockUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.InventoryResponseDTO;
import java.util.List;

public interface InventoryService {
    InventoryResponseDTO getInventory(String productId);

    List<InventoryResponseDTO> getAllInventory();

    InventoryResponseDTO updateInventory(String productId, StockUpdateRequestDTO request);

    void reduceStock(String productId, Integer quantity); // For internal use by OrderService

    void increaseStock(String productId, Integer quantity); // For internal use by OrderService (Cancellation)

    void initializeInventory(String productId, Integer quantity, Integer threshold);

    void deleteInventory(String productId);
}
