package org.elysium.backend.repos;

import org.elysium.backend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(String userId);

    List<Order> findByDateOfPurchase(Date date);

    List<Order> findByDateOfPurchaseBetween(LocalDateTime startDate, LocalDateTime endDate);
}
