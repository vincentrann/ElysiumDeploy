package org.elysium.backend.controllers;

import org.elysium.backend.models.Order;
import org.elysium.backend.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * Endpoint for checking out and creating an order.
     *
     * @param userId       The ID of the user performing the checkout.
     * @param creditCardId The ID of the credit card for payment.
     * @return The created order.
     */
    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@RequestParam String userId, @RequestParam Long creditCardId) {
        try {
            Order order = orderService.checkout(userId, creditCardId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable String userId) {
        List<Order> orders = orderService.findByUserId(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 No Content
        }
        return ResponseEntity.ok(orders);  // 200 OK
    }
}
