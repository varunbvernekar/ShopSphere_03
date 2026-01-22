package com.shopsphere.api.dto.requestDTO;

import com.shopsphere.api.enums.OrderStatus;
import com.shopsphere.api.entity.Address;
import com.shopsphere.api.entity.Order;
import com.shopsphere.api.entity.OrderItem;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public static Order toEntity(OrderRequestDTO request) {
        if (request == null) {
            return null;
        }

        List<OrderItem> items = Collections.emptyList();
        if (request.getItems() != null) {
            items = request.getItems().stream()
                    .map(OrderRequestDTO::toItemEntity)
                    .collect(Collectors.toList());
        }

        Order.OrderBuilder orderBuilder = Order.builder()
                .userId(request.getUserId())
                .placedOn(LocalDateTime.now())
                .amount(request.getAmount())
                .status(request.getStatus())
                .items(items)
                .estimatedDelivery(request.getEstimatedDelivery())
                .deliveryAddress(request.getDeliveryAddress());

        if (request.getLogistics() != null) {
            orderBuilder.logistics(com.shopsphere.api.entity.LogisticsInfo.builder()
                    .carrier(request.getLogistics().getCarrier())
                    .trackingId(request.getLogistics().getTrackingId())
                    .currentLocation(request.getLogistics().getCurrentLocation())
                    .build());
        }

        return orderBuilder.build();
    }

    private static OrderItem toItemEntity(OrderItemRequestDTO request) {
        return OrderItem.builder()
                .productId(request.getProductId())
                .name(request.getName())
                .image(request.getImage())
                .quantity(request.getQuantity())
                .color(request.getColor())
                .size(request.getSize())
                .material(request.getMaterial())
                .price(request.getPrice())
                .build();
    }
}
