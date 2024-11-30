package org.elysium.backend.services;

import org.elysium.backend.models.*;
import org.elysium.backend.repos.CartItemRepository;
import org.elysium.backend.repos.OrderItemRepository;
import org.elysium.backend.repos.OrderRepository;
import org.elysium.backend.repos.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service

public class OrderService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CreditCardService creditCardRepository;
    @Autowired
    private CreditCardService creditCardService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Order checkout(String userid, Long creditCardId)
    {
       List<CartItem> cartItemList = cartItemRepository.findByUserId(userid);
       if (cartItemList.isEmpty())
       {
           throw new RuntimeException("Cart is empty. Cannot proceed to checkout.");
       }
        // Step 2: Validate the selected credit card
        // Step 2: Check if the user has any credit cards
        List<CreditCard> userCreditCards = creditCardService.getCreditCardsByUserId(userid);
        if (userCreditCards.isEmpty()) {
            throw new RuntimeException("No credit cards found for the user. Please add a credit card to proceed.");
        }

        // Step 3: Validate the selected credit card
        CreditCard creditCard = creditCardService.getCreditCardById(creditCardId);
        if (!creditCard.getUser().getId().equals(userid)) {
            throw new RuntimeException("Unauthorized: Credit card does not belong to this user.");
        }


        // Ensure the credit card belongs to the user
        if (!creditCard.getUser().getId().equals(userid)) {
            throw new RuntimeException("Unauthorized: Credit card does not belong to this user.");
        }
       double totalPrice = 0;
       for (CartItem cartItem : cartItemList)
       {
           Product product = productRepository.findById(cartItem.getProduct().getId())
                   .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProduct().getId()));
           if(cartItem.getQuantity()>product.getStockQuantity())
           {
               throw new RuntimeException("Insufficient stock for product: " + product.getName());
           }
           totalPrice += cartItem.getQuantity() * product.getPrice();
       }
       Order order = new Order();
       order.setTotalPrice(totalPrice);
       order.setUserId(userid);
       order.setDateOfPurchase(new Date());
       order = orderRepository.save(order);
        // Step 4: Create Order Items and Update Inventory
        for (CartItem cartItem : cartItemList) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProduct().getId()));

            // Create OrderItem
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtTime(product.getPrice()); // or discount price if applicable
            orderItemRepository.save(orderItem);

            // Update Product Inventory
            int updatedStockQuantity = product.getStockQuantity() - cartItem.getQuantity();
            product.setStockQuantity(updatedStockQuantity);
            productRepository.save(product);
        }

        // Step 5: Clear the Cart
        cartItemRepository.deleteAll(cartItemList);

        // Return the Order as confirmation
        return order;
    }
    public List<Order> findByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
