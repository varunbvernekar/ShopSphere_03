package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.responseDTO.DashboardStatsResponseDTO;
import com.shopsphere.api.dto.responseDTO.ProductSalesDTO;
import com.shopsphere.api.services.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    private DashboardStatsResponseDTO statsResponse;
    private ProductSalesDTO productSalesDTO;

    @BeforeEach
    void setUp() {
        statsResponse = DashboardStatsResponseDTO.builder()
                .totalRevenue(5000.0) // Changed from totalSales
                .totalOrders(50L) // Changed from int to Long if needed, DTO uses Long
                .totalProducts(20L)
                .activeProducts(15L) // Replaced lowStock/pending with valid field
                .build();

        // ProductSalesDTO(name, image, timesOrdered, totalQuantity, totalRevenue)
        productSalesDTO = new ProductSalesDTO("Top Product", "img.jpg", 10L, 100L, 2000.0);
    }

    @Test
    void getDashboardStats_ShouldReturnStats() {
        when(dashboardService.getDashboardStats()).thenReturn(statsResponse);

        ResponseEntity<DashboardStatsResponseDTO> response = dashboardController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5000.0, response.getBody().getTotalRevenue());
        verify(dashboardService).getDashboardStats();
    }

    @Test
    void getTopSellingProducts_ShouldReturnList() {
        when(dashboardService.getTopSellingProducts(5)).thenReturn(Collections.singletonList(productSalesDTO));

        ResponseEntity<List<ProductSalesDTO>> response = dashboardController.getTopSellingProducts(5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Top Product", response.getBody().get(0).getName()); // Changed from getProductId
        verify(dashboardService).getTopSellingProducts(5);
    }
}
