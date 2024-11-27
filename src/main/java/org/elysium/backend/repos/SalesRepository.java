package org.elysium.backend.repos;

import org.elysium.backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface SalesRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUserId(String userId);

    @Query("SELECT DISTINCT o " +
       "FROM Orders o " +
       "JOIN OrderItems oi ON o.id = oi.order_id " +
       "JOIN products p ON oi.product_id = p.id " +
       "WHERE p.name = :productName")
    List<Order> findByProductName(String productName);

    List<Order> findByDate(Date date);

    List<Order> findByDateOfPurchaseBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = """
            SELECT *
            FROM Orders
            """)
    List<Order> findAllSales();

    

}


