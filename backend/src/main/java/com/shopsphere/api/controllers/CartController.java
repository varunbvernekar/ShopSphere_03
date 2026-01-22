package com.shopsphere.api.controllers;

import com.shopsphere.api.dto.requestDTO.AddToCartRequestDTO;
import com.shopsphere.api.dto.requestDTO.UpdateCartItemRequestDTO;
import com.shopsphere.api.dto.responseDTO.CartResponseDTO;
import com.shopsphere.api.entity.User;
import com.shopsphere.api.services.CartService;
import com.shopsphere.api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@lombok.extern.slf4j.Slf4j
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        String email = authentication.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return user.getId();
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        Long userId = getAuthenticatedUserId();
        log.info("Fetching cart for user ID: {}", userId);
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDTO> addToCart(@RequestBody AddToCartRequestDTO request) {
        Long userId = getAuthenticatedUserId();
        log.info("Adding item to cart for user ID: {}, Product ID: {}", userId, request.getProductId());
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    @PutMapping("/update/{itemId}")
    public ResponseEntity<CartResponseDTO> updateCartItem(@PathVariable Long itemId,
            @RequestBody UpdateCartItemRequestDTO request) {
        Long userId = getAuthenticatedUserId();
        log.info("Updating cart item ID: {} for user ID: {}", itemId, userId);
        return ResponseEntity.ok(cartService.updateCartItem(userId, itemId, request));
    }

    @DeleteMapping("/remove/{itemId}")
    public ResponseEntity<CartResponseDTO> removeFromCart(@PathVariable Long itemId) {
        Long userId = getAuthenticatedUserId();
        log.info("Removing cart item ID: {} for user ID: {}", itemId, userId);
        return ResponseEntity.ok(cartService.removeFromCart(userId, itemId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        Long userId = getAuthenticatedUserId();
        log.info("Clearing cart for user ID: {}", userId);
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }
}
