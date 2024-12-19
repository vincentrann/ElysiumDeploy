package org.elysium.backend.controllers;

import jakarta.servlet.http.HttpSession;
import org.elysium.backend.models.Cart;
import org.elysium.backend.models.CartItem;
import org.elysium.backend.models.Product;
import org.elysium.backend.services.CartService;
import org.elysium.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"})
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    /**
     * Get the cart for a specific user (guest or authenticated user)
     * 
     * @param userId  The user ID (guest or authenticated user)
     * @return ResponseEntity with the cart object
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> getCartItemsByUserId(@PathVariable String userId) {
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
        return ResponseEntity.ok(cartItems);
    }

    /**
     * Endpoint to add items to the user's cart.
     *
     * @param products  The list of products in the request body
     * @param userId    The user ID of the authenticated user
     * @return ResponseEntity containing the updated cart or error message
     */
    @PostMapping("/{userId}/add")
    public ResponseEntity<String> addItemToCart(
            @RequestBody String products,
            @PathVariable String userId) {

        try {
            // Call the service to process the products and add them to the user's cart
            cartService.addItemsToCart(products, userId);
            return ResponseEntity.ok("Items successfully added to user " + userId + "'s cart.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error adding items to cart: " + e.getMessage());
        }
    }


    @PostMapping("/{userId}/remove")
    public ResponseEntity<String> removeItemFromCart(
            @RequestBody String products, // Accepting a list of objects
            @PathVariable String userId) {
        try {
            // Call the service to process the products and remove them from the user's cart
            cartService.removeItemsFromCart(products, userId);
            return ResponseEntity.ok("Items successfully removed from user cart " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error removing items from cart: " + e.getMessage());
        }
    }

    /**
     * Transfer guest cart items to a logged-in user's cart.
     * // TODO - parse products into CartItem, fix products in header to pass product id, quantity, remove url, title, price
     * @param products
//     * @param userId  The ID of the logged-in user.
     * @return ResponseEntity with the updated cart for the authenticated user
     */
    @PostMapping("/{userId}/transfer")
    public ResponseEntity<String> transferGuestCartToUser(
            @RequestBody String products, // Accepting a list of objects
            @PathVariable String userId) {

        try {
            // Call the service to process the products and add them to the user's cart
            cartService.transferGuestCartToUser(products, userId);
            return ResponseEntity.ok("Guest cart transferred to user " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error transferring cart: " + e.getMessage());
        }
    }
}
