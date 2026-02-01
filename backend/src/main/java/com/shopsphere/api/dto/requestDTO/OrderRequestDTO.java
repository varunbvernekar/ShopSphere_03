package com.shopsphere.api.dto.requestDTO;

import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.entity.Address;
import lombok.Data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {
    private Long userId;
    private Double amount;
    private OrderStatus status; // If creating order, usually pending, but might be passed
    private List<OrderItemRequestDTO> items;
    private String estimatedDelivery;
    private Address deliveryAddress;
    private LogisticsInfoRequestDTO logistics;

}
