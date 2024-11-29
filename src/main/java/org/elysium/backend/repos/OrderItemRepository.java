package org.elysium.backend.repos;

import org.elysium.backend.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    // Find all order items by order ID
    List<OrderItem> findByOrderId(Integer orderId);

    // Find all order items for a specific product
    List<OrderItem> findByProductId(String productId);
}