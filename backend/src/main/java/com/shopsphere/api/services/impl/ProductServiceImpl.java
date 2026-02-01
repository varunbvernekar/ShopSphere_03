package com.shopsphere.api.services.impl;

import com.shopsphere.api.dto.requestDTO.ProductRequestDTO;
import com.shopsphere.api.dto.requestDTO.StockUpdateRequestDTO;
import com.shopsphere.api.dto.responseDTO.ProductResponseDTO;
import com.shopsphere.api.entity.Product;
import com.shopsphere.api.repositories.ProductRepository;
import com.shopsphere.api.services.InventoryService;
import com.shopsphere.api.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll().stream()
                .map(product -> {
                    ProductResponseDTO response = ProductResponseDTO.fromEntity(product);
                    // Enrich with inventory data
                    var inventory = inventoryService.getInventory(product.getProductId());
                    response.setStockLevel(inventory.getQuantity());
                    response.setReorderThreshold(inventory.getReorderThreshold());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponseDTO> getProductById(String id) {
        log.debug("Fetching product by ID: {}", id);
        return productRepository.findById(id)
                .map(product -> {
                    ProductResponseDTO response = ProductResponseDTO.fromEntity(product);
                    var inventory = inventoryService.getInventory(product.getProductId());
                    response.setStockLevel(inventory.getQuantity());
                    response.setReorderThreshold(inventory.getReorderThreshold());
                    return response;
                });
    }

    @Override
    @Transactional
    public ProductResponseDTO saveProduct(ProductRequestDTO productRequest) {
        log.info("Creating new product: {}", productRequest.getName());
        if (productRequest.getCustomOptions() != null) {
            log.info("Received {} custom option groups", productRequest.getCustomOptions().size());
            productRequest.getCustomOptions().forEach(g -> log.info("Option Group: {}, Items: {}", g.getType(),
                    g.getOptions() != null ? g.getOptions().size() : 0));
        } else {
            log.warn("No custom options received in request");
        }
        Product product = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .category(productRequest.getCategory())
                .basePrice(productRequest.getBasePrice())
                .previewImage(productRequest.getPreviewImage())
                .customOptions(productRequest.getCustomOptions())
                .isActive(productRequest.getIsActive())
                .build();

        String generatedId = "P" + System.currentTimeMillis();
        product.setProductId(generatedId);

        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }

        Product savedProduct = productRepository.save(product);
        log.info("Product created with ID: {}", generatedId);

        // Initialize Inventory
        inventoryService.initializeInventory(generatedId, productRequest.getStockLevel(),
                productRequest.getReorderThreshold());

        // Return response with inventory info
        ProductResponseDTO response = ProductResponseDTO.fromEntity(savedProduct);
        return response;
    }

    @Override
    @Transactional
    public ProductResponseDTO updateProduct(String id, ProductRequestDTO productRequest) {
        log.info("Updating product ID: {}", id);
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new RuntimeException("Product not found");
                });

        Product updatedState = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .category(productRequest.getCategory())
                .basePrice(productRequest.getBasePrice())
                .previewImage(productRequest.getPreviewImage())
                .customOptions(productRequest.getCustomOptions())
                .isActive(productRequest.getIsActive())
                .build();
        updatedState.setProductId(id);

        Product savedProduct = productRepository.save(updatedState);

        // Update inventory if provided in request?
        if (productRequest.getStockLevel() != null || productRequest.getReorderThreshold() != null) {
            log.info("Updating inventory for product ID: {}", id);
            var stockReq = new StockUpdateRequestDTO();
            stockReq.setQuantity(productRequest.getStockLevel());
            stockReq.setThreshold(productRequest.getReorderThreshold());
            inventoryService.updateInventory(id, stockReq);
        }

        ProductResponseDTO response = ProductResponseDTO.fromEntity(savedProduct);
        var inventory = inventoryService.getInventory(id);
        response.setStockLevel(inventory.getQuantity());
        response.setReorderThreshold(inventory.getReorderThreshold());
        log.info("Product ID: {} updated successfully", id);
        return response;
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        log.warn("Deleting product ID: {}", id);
        productRepository.deleteById(id);
        inventoryService.deleteInventory(id);
        log.info("Product ID: {} deleted successfully", id);
    }
}
