package com.shopsphere.api.repositories;

import com.shopsphere.api.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import com.shopsphere.api.dto.responseDTO.ProductSalesDTO;
import org.springframework.data.domain.Pageable;

public interface OrderRepository extends JpaRepository<Order, Long> {
        List<Order> findByUserId(Long userId);

        @Query("SELECT new com.shopsphere.api.dto.responseDTO.ProductSalesDTO(i.name, i.image, COUNT(o), SUM(i.quantity), SUM(i.price * i.quantity)) "
                        +
                        "FROM Order o JOIN o.items i " +
                        "WHERE o.status = com.shopsphere.api.enums.OrderStatus.Delivered " +
                        "GROUP BY i.name, i.image " +
                        "ORDER BY SUM(i.quantity) DESC")
        List<ProductSalesDTO> findTopSellingProducts(Pageable pageable);
}
