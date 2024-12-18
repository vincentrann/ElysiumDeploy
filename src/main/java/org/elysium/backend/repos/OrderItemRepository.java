package org.elysium.backend.repos;

import org.elysium.backend.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findById(int orderId);

    List<OrderItem> findByProductId(String productId);

    List<OrderItem> findByProductName(String productName);

    List<OrderItem> findByOrderDateOfPurchase(LocalDate dateOfPurchase);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.order o JOIN FETCH oi.product p WHERE o.id = :orderId")
    List<OrderItem> findOrderItemsByOrderId(@Param("orderId") int orderId);

    @Query("SELECT oi FROM OrderItem oi JOIN FETCH oi.order o JOIN FETCH oi.product p WHERE o.userId = :userId")
    List<OrderItem> findOrderItemsByUserId(@Param("userId") String userId);


}