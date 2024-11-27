package org.elysium.backend.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Date;

import org.springframework.security.core.Authentication;
import org.elysium.backend.models.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.elysium.backend.services.AdminCustomerService;
import org.elysium.backend.services.ProductService;
import org.elysium.backend.services.SalesService;
import org.elysium.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import jakarta.servlet.http.HttpSession;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private SalesService salesService;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminCustomerService adminCustomerService;

    @Autowired
    private ProductService productService;

    //landing page for admins
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to the Admin Dashboard!");
        response.put("username", authentication.getName());
        response.put("roles", authentication.getAuthorities());

        return ResponseEntity.ok(response);
    }
    
    //checking sales history
    @GetMapping("/OrderHistory")
    public List<Order> viewSalesHistory(HttpSession session){
        List<Order> orders = salesService.getAllOrders();
        return orders;
    }

    //filter by customers
    @GetMapping("/OrderHistory/user/{username}")
    public List<Order> getUserOrderHistory(@PathVariable String username){
        return salesService.filterOrdersByUser(username);
    }

    //filter by product
    @GetMapping("/OrderHistory/product/{productName}")
    public List<Order> getProductOrderHistory(@PathVariable String productName){
        return salesService.filterOrdersByProduct(productName);
    }

    //filter by date
    @GetMapping("/OrderHistory/date/{date}")
    public List<Order> getDateOrderHistory(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date specificDate){
        return salesService.filterOrdersByDate(specificDate);
    }

    //view customer info
    @GetMapping("/customers/{username}")
    public ResponseEntity<Map<String, Object>> getCustomerAccount(@PathVariable String username){
        User user = adminCustomerService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<Order> orderHistory = salesService.getUserOrders(user.getId());

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
