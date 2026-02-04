package com.shopsphere.api.dto.responseDTO;

import lombok.Builder;
import lombok.Data;
import com.shopsphere.api.entity.Inventory;

@Data
@Builder
public class InventoryResponseDTO {
    private String productId;
    private String productName;
    private Integer quantity;
    private Integer reorderThreshold;

    public static InventoryResponseDTO fromEntity(Inventory inventory, String productName) {
        if (inventory == null) {
            return null;
        }
        return InventoryResponseDTO.builder()
                .productId(inventory.getProductId())
                .productName(productName)
                .quantity(inventory.getQuantity())
                .reorderThreshold(inventory.getReorderThreshold())
                .build();
    }
}
