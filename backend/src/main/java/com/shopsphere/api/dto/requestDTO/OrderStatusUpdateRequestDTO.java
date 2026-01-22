package com.shopsphere.api.dto.requestDTO;

import com.shopsphere.api.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderStatusUpdateRequestDTO {
    private OrderStatus status;
}
