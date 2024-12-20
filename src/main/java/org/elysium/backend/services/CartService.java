package org.elysium.backend.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.elysium.backend.models.Cart;
import org.elysium.backend.models.CartItem;
import org.elysium.backend.models.Product;
import org.elysium.backend.repos.CartRepository;
import org.elysium.backend.repos.CartItemRepository;
import org.elysium.backend.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
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

    @Autowired
    private CartItemService cartItemService;

    /**
     * Get or create cart for guest or user.
     */
    public List<CartItem> getCartItemsByUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID must be provided.");
        }

        // Fetch all cart items associated with the user's cart
        return cartItemRepository.findByUserId(userId);
    }

    /**
     * Add multiple items to the user's cart.
     *
     * @param products The list of product data in JSON string format
     * @param userId   The ID of the user to whom the cart belongs
     */
    public void addItemsToCart(String products, String userId) {
        // Convert the string into a list of maps using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> productList;

        try {
            productList = objectMapper.readValue(products, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse products JSON string", e);
        }

        // Find or create the user's cart
        Cart userCart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });

        // Loop through the parsed product data
        for (Map<String, Object> productData : productList) {
            String title = productData.get("title").toString();
            int quantity = Integer.parseInt(productData.get("quantity").toString());

            Product product = productRepository.findByName(title)
                    .orElseThrow(() -> new RuntimeException("Product not found with title: " + title));

            cartItemService.createAndSaveCartItem(userCart, product.getId(), quantity, userId);
        }

        // Save the updated user cart
        cartRepository.save(userCart);
    }

    public void transferGuestCartToUser(String products, String userId) {
        // Convert the string into a list of maps using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> productList;

        try {
            productList = objectMapper.readValue(products, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse products JSON string", e);
        }

        // Find or create the user's cart
        Cart userCart = cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });

        // Loop through the parsed product data
        for (Map<String, Object> productData : productList) {
            String title = productData.get("title").toString();
            int quantity = Integer.parseInt(productData.get("quantity").toString());

            Product product = productRepository.findByName(title)
                    .orElseThrow(() -> new RuntimeException("Product not found with title: " + title));

            cartItemService.createAndSaveCartItem(userCart, product.getId(), quantity, userId);
        }

        // Save the updated user cart
        cartRepository.save(userCart);
    }


    public void removeItemsFromCart(String products, String userId) {
        // Convert the string into a list of maps using Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> productList;

        try {
            productList = objectMapper.readValue(products, new TypeReference<List<Map<String, Object>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse products JSON string", e);
        }

        // Get the user's cart
        Cart userCart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User cart not found for userId: " + userId));

        // Loop through the parsed product data
        for (Map<String, Object> productData : productList) {
            String title = productData.get("title").toString();

            // Find the product by its name
            Product product = productRepository.findByName(title)
                    .orElseThrow(() -> new RuntimeException("Product not found with title: " + title));

            // Find the CartItem in the user's cart
            Optional<CartItem> cartItemOpt = cartItemRepository.findByProductIdAndCartId(product.getId(), userCart.getId());
            if (cartItemOpt.isPresent()) {
                CartItem cartItem = cartItemOpt.get();
                cartItemRepository.delete(cartItem); // Delete the CartItem from the database
            } else {
                System.out.println("CartItem not found for product title: " + title);
            }
        }

        // Save the updated user cart
        cartRepository.save(userCart);
    }

}
