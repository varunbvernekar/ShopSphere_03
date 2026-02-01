package com.shopsphere.api.repositories;

import com.shopsphere.api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
        List<Order> findByUserId(Long userId);

        @org.springframework.data.jpa.repository.Query("SELECT new com.shopsphere.api.dto.responseDTO.ProductSalesDTO(i.name, i.image, COUNT(o), SUM(i.quantity), SUM(i.price * i.quantity)) "
                        +
                        "FROM Order o JOIN o.items i " +
                        "WHERE o.status = com.shopsphere.api.enums.OrderStatus.Delivered " +
                        "GROUP BY i.name, i.image " +
                        "ORDER BY SUM(i.quantity) DESC")
        List<com.shopsphere.api.dto.responseDTO.ProductSalesDTO> findTopSellingProducts(
                        org.springframework.data.domain.Pageable pageable);
}
