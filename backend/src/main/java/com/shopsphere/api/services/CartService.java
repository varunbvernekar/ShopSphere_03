package com.shopsphere.api.services;

import com.shopsphere.api.dto.requestDTO.AddToCartRequestDTO;
import com.shopsphere.api.dto.requestDTO.UpdateCartItemRequestDTO;
import com.shopsphere.api.dto.responseDTO.CartResponseDTO;

public interface CartService {
    CartResponseDTO getCart(Long userId);

    CartResponseDTO addToCart(Long userId, AddToCartRequestDTO request);

    CartResponseDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequestDTO request);

    CartResponseDTO removeFromCart(Long userId, Long cartItemId);

    void clearCart(Long userId);
}
