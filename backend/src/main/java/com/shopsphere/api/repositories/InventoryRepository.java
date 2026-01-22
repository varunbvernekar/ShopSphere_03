package com.shopsphere.api.repositories;

import com.shopsphere.api.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(String productId);

    void deleteByProductId(String productId);
}
