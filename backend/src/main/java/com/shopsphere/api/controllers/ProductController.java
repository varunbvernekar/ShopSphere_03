package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.ProductRequestDTO;
import com.shopsphere.api.dto.responseDTO.ProductResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.shopsphere.api.services.InventoryService;
import com.shopsphere.api.services.ProductService;
import com.shopsphere.api.services.FileStorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@lombok.extern.slf4j.Slf4j
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        log.info("Fetching all products");
        List<ProductResponseDTO> products = productService.getAllProducts();
        products.forEach(this::populateInventoryData);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable String id) {
        log.debug("Fetching product details for ID: {}", id);
        return productService.getProductById(id)
                .map(product -> {
                    populateInventoryData(product);
                    return ResponseEntity.ok(product);
                })
                .orElseGet(() -> {
                    log.error("Product not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ProductResponseDTO> createProductWithImage(
            @RequestPart("product") String productJson,
            @RequestPart(value = "image", required = false) MultipartFile image)
            throws IOException {
        log.info("Creating product with possible image upload");
        ProductRequestDTO productRequest = objectMapper.readValue(productJson, ProductRequestDTO.class);

        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.saveFile(image);
            productRequest.setPreviewImage(imagePath);
            log.info("Image uploaded and saved to: {}", imagePath);
        }

        ProductResponseDTO savedProduct = productService.saveProduct(productRequest);

        // Handle Inventory
        inventoryService.initializeInventory(savedProduct.getProductId(),
                productRequest.getStockLevel(),
                productRequest.getReorderThreshold());

        populateInventoryData(savedProduct);
        log.info("Product created successfully with ID: {}", savedProduct.getProductId());
        return ResponseEntity.ok(savedProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        log.info("Deleting product ID: {}", id);
        productService.deleteProduct(id);
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable String id,
            @RequestBody ProductRequestDTO productRequest) {
        log.info("Updating product ID: {}", id);
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequest);
        populateInventoryData(updatedProduct);
        return ResponseEntity.ok(updatedProduct);
    }

    private void populateInventoryData(ProductResponseDTO product) {
        try {
            var inventory = inventoryService.getInventory(product.getProductId());
            if (inventory != null) {
                product.setStockLevel(inventory.getQuantity());
                product.setReorderThreshold(inventory.getReorderThreshold());
            }
        } catch (Exception e) {
            log.error("Failed to fetch inventory for product ID: {}", product.getProductId(), e);
            product.setStockLevel(0);
            product.setReorderThreshold(0);
        }
    }
}
