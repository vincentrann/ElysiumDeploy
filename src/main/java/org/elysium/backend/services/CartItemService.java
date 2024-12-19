package org.elysium.backend.services;

import org.elysium.backend.models.Cart;
import org.elysium.backend.models.CartItem;
import org.elysium.backend.models.Product;
import org.elysium.backend.repos.CartItemRepository;
import org.elysium.backend.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Create and save a CartItem associated with a Cart.
     *
     * @param cart       The Cart to associate with the CartItem.
     * @param productId  The ID of the product to add.
     * @param quantity   The quantity of the product.
     * @param userId     The user ID associated with the CartItem.
     * @return The saved CartItem.
     */
    public CartItem createAndSaveCartItem(Cart cart, String productId, int quantity, String userId) {
        // Fetch the product from the database
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));

        // Check if the CartItem already exists for this product in the cart
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Update quantity if CartItem already exists
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            return cartItemRepository.save(cartItem);
        } else {
            // Create a new CartItem
            CartItem cartItem = new CartItem(cart, product, quantity, product.getPrice());
            cartItem.setUserId(userId); // Set the user ID
            cart.getCartItems().add(cartItem); // Add to the Cart's list
            return cartItemRepository.save(cartItem); // Save and return the new CartItem
        }
    }
}
