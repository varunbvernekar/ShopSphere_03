package com.shopsphere.api.dto.requestDTO;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {
    private String productId;
    private String name;
    private String image;
    private Integer quantity;
    private String color;
    private String size;
    private String material;
    private Double price;
}
