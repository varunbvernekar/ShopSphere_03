package com.shopsphere.api.dto.responseDTO;

import com.shopsphere.api.entity.CustomOptionGroup;
import com.shopsphere.api.entity.Product;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
    private String productId;
    private String name;
    private String description;
    private String category;
    private Double basePrice;
    private String previewImage;
    private List<CustomOptionGroup> customOptions;
    private Integer stockLevel;
    private Integer reorderThreshold;
    private Boolean isActive;

    public static ProductResponseDTO fromEntity(Product product) {
        if (product == null) {
            return null;
        }
        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .basePrice(product.getBasePrice())
                .previewImage(product.getPreviewImage())
                .customOptions(product.getCustomOptions())
                // Stock fields are handled by service aggregation, default to 0 here/null
                .stockLevel(0)
                .reorderThreshold(0)
                .isActive(product.getIsActive())
                .build();
    }
}
