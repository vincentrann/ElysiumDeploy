package org.elysium.backend.models;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    private String id;
    private String name;
    @Column(columnDefinition = "Text")
    private String description;
    private double price;
    private int stockQuantity;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String imageUrl;
    private double discountPrice;
    private double weight;
    private String dimensions;
    private String brand;
    private double rating;
    private int numberOfReviews;


public String getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
