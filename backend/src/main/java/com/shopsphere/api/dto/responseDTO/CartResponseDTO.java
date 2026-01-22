package com.shopsphere.api.dto.responseDTO;

import lombok.Data;
import java.util.List;

@Data
public class CartResponseDTO {
    private Long id;
    private Long userId;
    private List<CartItemResponseDTO> items;
    private Double subtotal;
    private Double tax;
    private Double shipping;
    private Double totalAmount;
}
