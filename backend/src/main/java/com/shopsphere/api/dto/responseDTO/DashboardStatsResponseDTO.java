package com.shopsphere.api.dto.responseDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponseDTO {
    private Long totalOrders;
    private Double totalRevenue;
    private Long totalProducts;
    private Long activeProducts;
}
