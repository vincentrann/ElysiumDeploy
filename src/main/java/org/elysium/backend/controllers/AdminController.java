package org.elysium.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.elysium.backend.DataTransferObjects.OrderWithItemsDto;
import org.elysium.backend.models.*;
import org.elysium.backend.services.AdminCustomerService;
import org.elysium.backend.services.OrderService;
import org.elysium.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminCustomerService adminCustomerService;

    @Autowired
    private ProductService productService;

    @Autowired OrderService orderService;

    //landing page for admins
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to the Admin Dashboard!");
        return ResponseEntity.ok(response);
    }
    //checking order history
    @GetMapping("/OrderHistory")
    public List<OrderWithItemsDto> viewOrderHistory(){
        return orderService.getAllOrdersWithItems();
    }

    //filter orders with order number
    @GetMapping("/OrderHistory/{orderId}")
    public OrderWithItemsDto getOrderWithItems(@PathVariable int orderId) {
        return orderService.getOrderWithItems(orderId);
    }

    //filter orders by customers
    @GetMapping("/OrderHistory/user/{username}")
    public List<OrderWithItemsDto> getUserOrderHistory(@PathVariable String username){
        return orderService.getOrdersWithItemsByUsername(username);
    }

    //filter by product
    @GetMapping("/OrderHistory/product/{productName}")
    public List<OrderWithItemsDto> getProductOrderHistory(@PathVariable String productName){
        return orderService.getOrdersWithItemsByProductName(productName);
    }
    
    //filter by date
    @GetMapping("/OrderHistory/date/{date}")
    public List<OrderWithItemsDto> getDateOrderHistory(@PathVariable String specificDate){
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = dateFormat.parse(specificDate);
            
            return orderService.getOrdersWithItemsByDate(parsedDate);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd.");
        }
    }
    //view customer info
    @GetMapping("/customers/{username}")
    public ResponseEntity<Map<String, Object>> getCustomerAccount(@PathVariable String username){
        User user = adminCustomerService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<OrderWithItemsDto> orderHistory = orderService.getOrdersWithItemsByUsername(username);

        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("userInfo", user);
        customerDetails.put("orderHistory", orderHistory);

        return ResponseEntity.ok(customerDetails);

    }

    //update customer info
    @PutMapping("/customers/edit/{username}")
    public ResponseEntity<String> updateCustomerInfo(@PathVariable String username, @RequestBody User updatedUser) {
        boolean updated = adminCustomerService.adminUpdateUser(username, updatedUser);
        if (updated){
            return ResponseEntity.ok("Customer information updated successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer not found.");
        }
    }

    //maintain inventory
    //if quantityChange is negative, reduces the stock
    @PatchMapping("inventory/{productName}")
    public ResponseEntity<Product> updateProductQuantity(@PathVariable String productName, @RequestParam int quantityChange){
        try{
            Product updateProduct = productService.updateProductQuantity(productName, quantityChange);
            return ResponseEntity.ok(updateProduct);
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
