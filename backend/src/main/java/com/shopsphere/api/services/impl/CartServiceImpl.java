package com.shopsphere.api.services.impl;

import com.shopsphere.api.dto.requestDTO.AddToCartRequestDTO;
import com.shopsphere.api.dto.requestDTO.UpdateCartItemRequestDTO;
import com.shopsphere.api.dto.responseDTO.CartItemResponseDTO;
import com.shopsphere.api.dto.responseDTO.CartResponseDTO;
import com.shopsphere.api.entity.Cart;
import com.shopsphere.api.entity.CartItem;
import com.shopsphere.api.entity.Product;
import com.shopsphere.api.repositories.CartRepository;
import com.shopsphere.api.repositories.ProductRepository;
import com.shopsphere.api.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.shopsphere.api.services.InventoryService;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public CartResponseDTO getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        syncCartPrices(cart);
        return mapToResponse(cart);
    }

    private void syncCartPrices(Cart cart) {
        boolean changed = false;
        Iterator<CartItem> iterator = cart.getItems().iterator();

        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            Optional<Product> productOpt = productRepository.findById(item.getProductId());

            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                // Check for price change
                if (!item.getPrice().equals(product.getBasePrice())) {
                    item.setPrice(product.getBasePrice());
                    changed = true;
                }
                // Check for name change
                if (!item.getName().equals(product.getName())) {
                    item.setName(product.getName());
                    changed = true;
                }
                // Check for image change
                if (item.getImage() == null || !item.getImage().equals(product.getPreviewImage())) {
                    item.setImage(product.getPreviewImage());
                    changed = true;
                }
            } else {
                // Product no longer exists, remove from cart
                iterator.remove();
                changed = true;
            }
        }

        if (changed) {
            updateCartTotal(cart);
            cartRepository.save(cart);
        }
    }

    @Override
    @Transactional
    public CartResponseDTO addToCart(Long userId, AddToCartRequestDTO request) {
        log.info("Adding to cart. User: {}, Product: {}, Color: {}, Size: {}, Material: {}",
                userId, request.getProductId(), request.getColor(), request.getSize(), request.getMaterial());
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check Inventory
        int availableStock = inventoryService.getInventory(product.getProductId()).getQuantity();

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()) &&
                        Objects.equals(item.getColor(), request.getColor()) &&
                        Objects.equals(item.getSize(), request.getSize()) &&
                        Objects.equals(item.getMaterial(), request.getMaterial()))
                .findFirst();

        int currentCartQuantity = existingItem.map(CartItem::getQuantity).orElse(0);
        int requestedTotal = currentCartQuantity + request.getQuantity();

        if (requestedTotal > availableStock) {
            throw new RuntimeException("Insufficient stock. Available: " + availableStock);
        }

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(requestedTotal);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(product.getProductId())
                    .name(product.getName())
                    .image(product.getPreviewImage())
                    .price(product.getBasePrice())
                    .price(product.getBasePrice())
                    .quantity(request.getQuantity())
                    .color(request.getColor())
                    .size(request.getSize())
                    .material(request.getMaterial())
                    .build();
            cart.getItems().add(newItem);
        }

        updateCartTotal(cart);
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequestDTO request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        // Check Inventory
        int availableStock = inventoryService.getInventory(item.getProductId()).getQuantity();

        if (request.getQuantity() > availableStock) {
            throw new RuntimeException("Insufficient stock. Available: " + availableStock);
        }

        item.setQuantity(request.getQuantity());
        updateCartTotal(cart);
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public CartResponseDTO removeFromCart(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);
        boolean removed = cart.getItems().removeIf(item -> item.getId().equals(cartItemId));

        if (!removed) {
            throw new RuntimeException("Cart item not found or does not belong to this cart");
        }

        updateCartTotal(cart);
        cartRepository.save(cart);
        return mapToResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().clear();
        updateCartTotal(cart);
        cartRepository.save(cart);
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }

    private void updateCartTotal(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmount(total);
    }

    private CartResponseDTO mapToResponse(Cart cart) {
        CartResponseDTO response = new CartResponseDTO();
        response.setId(cart.getId());
        response.setUserId(cart.getUserId());

        List<CartItemResponseDTO> itemResponses = cart.getItems().stream()
                .map(this::mapItemToResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        // Core Business Logic for Pricing
        double subtotal = itemResponses.stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        double tax = subtotal * 0.10;
        double shipping = itemResponses.isEmpty() ? 0.0 : 50.0;
        double totalAmount = subtotal + tax + shipping;

        response.setSubtotal(subtotal);
        response.setTax(tax);
        response.setShipping(shipping);
        response.setTotalAmount(totalAmount);

        return response;
    }

    private CartItemResponseDTO mapItemToResponse(CartItem item) {
        CartItemResponseDTO response = new CartItemResponseDTO();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setName(item.getName());
        response.setImage(item.getImage());
        response.setPrice(item.getPrice());
        response.setQuantity(item.getQuantity());
        response.setColor(item.getColor());
        response.setSize(item.getSize());
        response.setMaterial(item.getMaterial());
        return response;
    }
}
