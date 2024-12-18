package org.elysium.backend.repos;

import org.elysium.backend.models.Order;
import org.elysium.backend.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Find all orders for a specific user by user ID
    List<Order> findByUserId(String userId);

    @Query("SELECT oi FROM order_item oi")
    List<OrderItem> findAllOrderItems();

    @Query("SELECT o FROM Order o WHERE o.id = :orderId")
    Order findOrderById(@Param("orderId") int orderId);

    @Query("SELECT o FROM Order o WHERE o.userId = (SELECT u.id FROM User u WHERE u.username = :username)")
    List<Order> findOrdersByUsername(@Param("username") String username);

    @Query("SELECT oi FROM OrderItem oi JOIN oi.product p WHERE p.name = :productName")
    List<OrderItem> findOrderItemsByProductName(@Param("productName") String productName);

    @Query("SELECT o FROM Order o WHERE o.dateOfPurchase = :specificDate")
    List<Order> findOrdersBySpecificDate(@Param("specificDate") Date specificDate);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findOrderItemsByOrderId(@Param("orderId") int orderId);

    

    
    List<Order> findByDateOfPurchase(Date date);

    List<Order> findByDateOfPurchaseBetween(LocalDateTime startDate, LocalDateTime endDate);
}
