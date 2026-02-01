package com.shopsphere.api.dto.requestDTO;

import com.shopsphere.api.entity.CustomOptionGroup;

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

}
