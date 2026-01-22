package com.shopsphere.api.dto.responseDTO;

import lombok.Builder;
import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long id;
    private String productId;
    private String name;
    private String image;
    private Integer quantity;
    private String color;
    private String size;
    private String material;
    private Double price;
}
