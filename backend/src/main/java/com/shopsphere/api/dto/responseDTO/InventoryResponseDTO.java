package com.shopsphere.api.dto.responseDTO;

import lombok.Builder;
import lombok.Data;
import com.shopsphere.api.entity.Inventory;

@Data
@Builder
public class InventoryResponseDTO {
    private String productId;
    private Integer quantity;
    private Integer reorderThreshold;

    public static InventoryResponseDTO fromEntity(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        return InventoryResponseDTO.builder()
                .productId(inventory.getProductId())
                .quantity(inventory.getQuantity())
                .reorderThreshold(inventory.getReorderThreshold())
                .build();
    }
}
