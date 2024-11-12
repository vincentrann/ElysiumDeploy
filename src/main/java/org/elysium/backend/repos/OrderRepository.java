package org.elysium.backend.repos;

import org.elysium.backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Custom query methods if needed

    // Find all orders for a specific user by user ID
    List<Order> findByUserId(String userId);

    // Find all orders within a specific date range (if needed in the future)
    // @Query annotation can be used for custom queries if required
}
