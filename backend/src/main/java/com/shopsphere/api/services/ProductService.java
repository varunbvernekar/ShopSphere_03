package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.ProductRequestDTO;
import com.shopsphere.api.dto.responseDTO.ProductResponseDTO;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<ProductResponseDTO> getAllProducts();

    Optional<ProductResponseDTO> getProductById(String id);

    ProductResponseDTO saveProduct(ProductRequestDTO productRequest);

    ProductResponseDTO updateProduct(String id, ProductRequestDTO productRequest);

    void deleteProduct(String id);

}
