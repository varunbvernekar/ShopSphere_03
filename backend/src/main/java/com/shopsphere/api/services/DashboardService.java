package com.shopsphere.api.services;

import com.shopsphere.api.dto.responseDTO.DashboardStatsResponseDTO;
import com.shopsphere.api.dto.responseDTO.ProductSalesDTO;
import java.util.List;

public interface DashboardService {
    DashboardStatsResponseDTO getDashboardStats();

    List<ProductSalesDTO> getTopSellingProducts(int limit);
}
