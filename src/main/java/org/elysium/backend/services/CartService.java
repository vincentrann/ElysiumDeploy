package org.elysium.backend.services;

import org.elysium.backend.models.Cart;
import org.elysium.backend.models.CartItem;
import org.elysium.backend.models.Product;
import org.elysium.backend.repos.CartRepository;
import org.elysium.backend.repos.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    public Cart getOrCreateCart(String userId)
    {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            return cartRepository.save(newCart);
        });
    }
    public Cart addItem(String userId, Product product, int quantity)
    {
        Cart cart = getOrCreateCart(userId);
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

        cartRepository.save(cart);
        return cart;
    }

    public Cart removeItemFromCart(String userId, Long cartItemId) {
        Cart cart = getOrCreateCart(userId);

        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        cartItemRepository.deleteById(cartItemId);

        return cartRepository.save(cart);
    }

    public double calculateCartTotal(Cart cart) {
        return cart.getCartItems().stream()
                .mapToDouble(item -> item.getPriceAtTime() * item.getQuantity())
                .sum();
    }
}
