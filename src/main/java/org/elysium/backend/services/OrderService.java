package org.elysium.backend.services;

import org.elysium.backend.DataTransferObjects.OrderWithItemsDto;
import org.elysium.backend.models.*;
import org.elysium.backend.repos.CartItemRepository;
import org.elysium.backend.repos.OrderItemRepository;
import org.elysium.backend.repos.OrderRepository;
import org.elysium.backend.repos.ProductRepository;
import org.elysium.backend.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CreditCardService creditCardService;
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
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


    // get all Orders with their OrderItems
    public List<OrderItem> getAllOrdersWithItems(){
        return orderItemRepository.findAll();
    }

    // get Order with their OrderItems given orderId
    public List<OrderItem> getOrderWithItems(int orderId) {
        // // Fetch the Order
        // Order order = orderRepository.findOrderById(orderId);

        // // Fetch associated OrderItems
        // List<OrderItem> orderItems = orderRepository.findOrderItemsByOrderId(orderId);

        // // Return the combined data in a DTO
        // return new OrderWithItemsDto(order, orderItems);
        return orderItemRepository.findOrderItemsByOrderId(orderId);
    }

    // get Orders with their OrderItems given username
    public List<OrderItem> getOrdersWithItemsByUsername(String username) {
        // List<Order> orders = orderRepository.findOrdersByUsername(username);
        // List<OrderWithItemsDto> ordersWithItems = new ArrayList<>();

        // for (Order order : orders) {
        //     List<OrderItem> orderItems = orderRepository.findOrderItemsByOrderId(order.getId());
        //     ordersWithItems.add(new OrderWithItemsDto(order, orderItems));
        // }

        // return ordersWithItems;

        User user = userRepository.findByUsername(username).get();
        String userId = user.getId();
        return orderItemRepository.findOrderItemsByUserId(userId);
    }

    // get Orders with OrderItems that contain given product
    public List<OrderItem> getOrdersWithItemsByProductName(String productName) {
        // List<OrderItem> orderItems = orderRepository.findOrderItemsByProductName(productName);
        // List<OrderWithItemsDto> ordersWithItems = new ArrayList<>();

        // for (OrderItem orderItem : orderItems) {
        //     Order order = orderItem.getOrder();
        //     boolean orderExists = false;
        //     for (OrderWithItemsDto dto : ordersWithItems) {
        //         if (dto.getOrder().getId() == order.getId()) {
        //             dto.getOrderItems().add(orderItem);
        //             orderExists = true;
        //             break;
        //         }
        //     }

        //     if (!orderExists) {
        //         List<OrderItem> items = new ArrayList<>();
        //         items.add(orderItem);
        //         ordersWithItems.add(new OrderWithItemsDto(order, items));
        //     }
        // }

        return orderItemRepository.findByProductName(productName);
    }

    // get Orders with OrderItems on the specific date
    public List<OrderItem> getOrdersWithItemsByDate(LocalDate specificDate) {
        // List<Order> orders = orderRepository.findOrdersBySpecificDate(specificDate);

        // List<OrderWithItemsDto> ordersWithItems = new ArrayList<>();

        // for (Order order : orders) {
        //     List<OrderItem> orderItems = orderRepository.findOrderItemsByOrderId(order.getId());

        //     ordersWithItems.add(new OrderWithItemsDto(order, orderItems));
        // }

        return orderItemRepository.findByOrderDateOfPurchase(specificDate);
    }

    public List<Order> findByUserId(String userId) {
        return orderRepository.findByUserId(userId);

    }
}
