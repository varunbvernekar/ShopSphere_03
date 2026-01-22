package com.shopsphere.api.dto.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsInfoRequestDTO {
    private String carrier;
    private String trackingId;
    private String currentLocation;
}
