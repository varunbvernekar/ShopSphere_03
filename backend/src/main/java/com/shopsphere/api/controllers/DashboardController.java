package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.responseDTO.DashboardStatsResponseDTO;
import com.shopsphere.api.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.shopsphere.api.dto.responseDTO.ProductSalesDTO;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsResponseDTO> getDashboardStats() {
        log.info("Fetching dashboard statistics");
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/products/top-selling")
    public ResponseEntity<List<ProductSalesDTO>> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.getTopSellingProducts(limit));
    }
}
