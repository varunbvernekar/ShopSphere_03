package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.LogisticsInfoRequestDTO;
import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;
import com.shopsphere.api.enums.OrderStatus;

public interface DeliveryService {
    OrderResponseDTO updateLogistics(Long orderId, LogisticsInfoRequestDTO request);

    OrderResponseDTO updateStatus(Long orderId, OrderStatus status);
}
