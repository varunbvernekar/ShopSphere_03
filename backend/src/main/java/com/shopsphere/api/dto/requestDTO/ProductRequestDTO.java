package com.shopsphere.api.dto.requestDTO;

import com.shopsphere.api.entity.CustomOptionGroup;
import com.shopsphere.api.entity.Product;
import lombok.Data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
    private String name;
    private String description;
    private String category;
    private Double basePrice;
    private String previewImage;
    private List<CustomOptionGroup> customOptions;
    private Integer stockLevel;
    private Integer reorderThreshold;
    private Boolean isActive;

    public static Product toEntity(ProductRequestDTO request) {
        if (request == null) {
            return null;
        }
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .basePrice(request.getBasePrice())
                .previewImage(request.getPreviewImage())
                .customOptions(request.getCustomOptions())
                .isActive(request.getIsActive())
                .build();
    }
}
