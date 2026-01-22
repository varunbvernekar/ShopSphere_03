package com.shopsphere.api.dto.responseDTO;

import lombok.Data;

@Data
public class CartItemResponseDTO {
    private Long id;
    private String productId;
    private String name;
    private String image;
    private Integer quantity;
    private Double price;
    private String color;
    private String size;
    private String material;
}
