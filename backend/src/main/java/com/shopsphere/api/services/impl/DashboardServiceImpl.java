package com.shopsphere.api.services.impl;

import com.shopsphere.api.dto.responseDTO.DashboardStatsResponseDTO;
import com.shopsphere.api.entity.Order;
import com.shopsphere.api.entity.Product;
import com.shopsphere.api.repositories.OrderRepository;
import com.shopsphere.api.repositories.ProductRepository;

import com.shopsphere.api.dto.responseDTO.ProductSalesDTO;
import com.shopsphere.api.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;

        @Override
        public DashboardStatsResponseDTO getDashboardStats() {
                List<Order> allOrders = orderRepository.findAll();
                List<Product> allProducts = productRepository.findAll();

                long totalOrders = allOrders.size();

                double totalRevenue = allOrders.stream()
                                .mapToDouble(Order::getAmount)
                                .sum();

                long totalProducts = allProducts.size();

                long activeProducts = allProducts.stream()
                                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                                .count();

                return DashboardStatsResponseDTO.builder()
                                .totalOrders(totalOrders)
                                .totalRevenue(totalRevenue)
                                .totalProducts(totalProducts)
                                .activeProducts(activeProducts)
                                .build();
        }

        @Override
        public List<ProductSalesDTO> getTopSellingProducts(int limit) {
                return orderRepository.findTopSellingProducts(PageRequest.of(0, limit));
        }
}
