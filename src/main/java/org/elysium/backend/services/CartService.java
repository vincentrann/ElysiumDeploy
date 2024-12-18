package org.elysium.backend.services;

import org.elysium.backend.models.Cart;
import org.elysium.backend.models.CartItem;
import org.elysium.backend.models.Product;
import org.elysium.backend.repos.CartRepository;
import org.elysium.backend.repos.CartItemRepository;
import org.elysium.backend.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // Handle guest cart retrieval without database interaction
    public Cart getOrCreateCart(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("Guest ID or User ID must be provided.");
        }

        if (userId.startsWith("guest-")) {
            return createTemporaryGuestCart(userId); // Create a temporary guest cart
        }

        // Find or create a cart for logged-in users
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });
    }

    private Cart createTemporaryGuestCart(String guestId) {
        Cart guestCart = new Cart();
        guestCart.setUserId(guestId); // Set a temporary guest ID
        guestCart.setCartItems(new ArrayList<>()); // Initialize empty items
        return guestCart; // No saving to database
    }

    public Cart addItem(String userId, Product product, int quantity) {
        Cart cart = getOrCreateCart(userId);

        if (userId.startsWith("guest-")) {
            // For guest users, only return the updated cart (in-memory logic)
            cart.getCartItems().add(new CartItem(cart, product, quantity, product.getPrice()));
            return cart;
        }

        // Logic for logged-in users (saved in database)
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem cartItem = new CartItem(cart, product, quantity, product.getPrice());
            cart.getCartItems().add(cartItem);
            cartItemRepository.save(cartItem);
        }

        return cartRepository.save(cart);
    }
    /**
     * Transfers guest cart items to a user's cart.
     *
     * @param guestId The guest ID (e.g., "guest-<random>").
     * @param userId  The ID of the logged-in user.
     */
    public void transferGuestCartToUser(String guestId, String userId) {
        // Find the guest cart
        Cart guestCart = cartRepository.findByUserId(guestId)
                .orElseThrow(() -> new RuntimeException("Guest cart not found"));

        // Find or create the user's cart
        Cart userCart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });

        // Transfer cart items
        List<CartItem> guestCartItems = guestCart.getCartItems();
        for (CartItem item : guestCartItems) {
            // Check if the product exists
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Add item to the user's cart
            CartItem newItem = new CartItem();
            newItem.setCart(userCart);
            newItem.setProduct(product);
            newItem.setQuantity(item.getQuantity());
            newItem.setPriceAtTime(item.getPriceAtTime());
            newItem.setUserId(userId);

            userCart.getCartItems().add(newItem);
        }

        // Save the updated user's cart
        cartRepository.save(userCart);

        // Delete the guest cart after transfer
        cartRepository.delete(guestCart);
    }

    public Cart removeItemFromCart(String userId, Long cartItemId) {
        if (userId.startsWith("guest-")) {
            throw new UnsupportedOperationException("Guest carts cannot remove items server-side.");
        }

        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        cartItemRepository.deleteById(cartItemId);
        return cartRepository.save(cart);
    }

}