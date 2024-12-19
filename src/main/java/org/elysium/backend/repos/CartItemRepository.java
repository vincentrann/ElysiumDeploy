package org.elysium.backend.repos;

import org.elysium.backend.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(String userId);

    /**
     * Finds a CartItem in a specific cart for a specific product.
     *
     * @param productId the ID of the product
     * @param cartId the ID of the cart
     * @return Optional<CartItem> if the cart item exists
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.product.id = :productId AND ci.cart.id = :cartId")
    Optional<CartItem> findByProductIdAndCartId(String productId, Long cartId);
}

