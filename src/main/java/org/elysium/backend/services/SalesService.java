package org.elysium.backend.services;


import org.elysium.backend.models.OrderItem;
import org.elysium.backend.models.Product;
import org.elysium.backend.models.User;

import org.elysium.backend.repos.OrderItemRepository;
import org.elysium.backend.repos.SalesRepository;
import org.elysium.backend.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.elysium.backend.models.Order;
@Service
public class SalesService {

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<Order> getAllOrders() {
        return salesRepository.findAllSales();
    }

    public List<Order> filterOrdersByUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return salesRepository.findByUserId(user.getId());
    }

    public List<Order> filterOrdersByProduct(String productName){
       return salesRepository.findByProductName(productName);
    }

    public List<Order> filterOrdersByDate(Date date){
        return salesRepository.findByDate(date);
    }

    public List<Order> filterOrdersByDateRange(LocalDate startDate, LocalDate endDate) {
        return salesRepository.findByDateOfPurchaseBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    public List<Order> getUserOrders(String userId){
        return salesRepository.findByUserId(userId);
    }
}
