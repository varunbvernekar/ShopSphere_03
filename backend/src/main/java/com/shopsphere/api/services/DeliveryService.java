package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.LogisticsInfoRequestDTO;
import com.shopsphere.api.dto.responseDTO.OrderResponseDTO;

public interface DeliveryService {
    OrderResponseDTO updateLogistics(Long orderId, LogisticsInfoRequestDTO request);

}
