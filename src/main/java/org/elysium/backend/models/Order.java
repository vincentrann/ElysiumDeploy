package org.elysium.backend.models;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "Orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private String userId; // Store userId as a reference instead of the User object

    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_purchase")
    private Date dateOfPurchase;

    @Column(name = "total_price")
    private double totalPrice;

    // @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    
    // private List<OrderItem> orderItems;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(Date dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    // public List<OrderItem> getOrderItems() {
    //     return orderItems;
    // }

    // public void setOrderItems(List<OrderItem> orderItems) {
    //     this.orderItems = orderItems;
    // }
}
