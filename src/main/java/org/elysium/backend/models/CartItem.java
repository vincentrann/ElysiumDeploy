package org.elysium.backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart; // Links to the Cart table

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product; // Links to the Product table

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price_at_time", nullable = false)
    private double priceAtTime;

    // Constructors
    public CartItem() {}

    public CartItem(Cart cart, Product product, int quantity, double priceAtTime) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.priceAtTime = priceAtTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPriceAtTime() {
        return priceAtTime;
    }

    public void setPriceAtTime(double priceAtTime) {
        this.priceAtTime = priceAtTime;
    }
}
