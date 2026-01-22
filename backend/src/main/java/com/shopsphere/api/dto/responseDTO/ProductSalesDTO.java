package com.shopsphere.api.dto.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSalesDTO {
    private String name;
    private String image;
    private Long timesOrdered;
    private Long totalQuantity;
    private Double totalRevenue;
}
