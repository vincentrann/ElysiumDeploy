package org.elysium.backend.controllers;

import org.elysium.backend.models.Cart;
import org.elysium.backend.models.Product;
import org.elysium.backend.services.CartService;
import org.elysium.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCartByUserId(@PathVariable String userId) {
        Cart cart = cartService.getOrCreateCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addItemToCart(@PathVariable String userId, @RequestParam String productId, @RequestParam int quantity) {
        Product product = productService.getProductById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        Cart updatedCart = cartService.addItem(userId, product, quantity);
        return ResponseEntity.ok(updatedCart);
    }

    @DeleteMapping("/{userId}/remove/{cartItemId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable String userId, @PathVariable Long cartItemId) {
        Cart updatedCart = cartService.removeItemFromCart(userId, cartItemId);
        return ResponseEntity.ok(updatedCart);
    }
}
