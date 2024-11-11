package org.elysium.backend.models;

import jakarta.persistence.*;

import java.util.Random;

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

    protected Product() {
        // No-argument constructor for JPA
    }
    // Private constructor to enforce use of Builder
    private Product(ProductBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.price = builder.price;
        this.stockQuantity = builder.stockQuantity;
        this.category = builder.category;
        this.imageUrl = builder.imageUrl;
        this.discountPrice = builder.discountPrice;
        this.weight = builder.weight;
        this.dimensions = builder.dimensions;
        this.brand = builder.brand;
        this.rating = builder.rating;
        this.numberOfReviews = builder.numberOfReviews;
    }

    // Getters for Product class (optional, depending on your use case)
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public double getWeight() {
        return weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getBrand() {
        return brand;
    }

    public double getRating() {
        return rating;
    }

    public int getNumberOfReviews() {
        return numberOfReviews;
    }
    public static String generateRandomId() {
        int length = 12; // Length of the ID
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // Allowed characters
        StringBuilder idBuilder = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            idBuilder.append(characters.charAt(randomIndex));
        }

        return idBuilder.toString();
    }

    public void setStockQuantity(int updatedStockQuantity) {
        this.stockQuantity = updatedStockQuantity;
    }

    // Static Inner Builder Class
    public static class ProductBuilder {
        private String id;
        private String name;
        private String description;
        private double price;
        private int stockQuantity;
        private Category category;
        private String imageUrl;
        private double discountPrice;
        private double weight;
        private String dimensions;
        private String brand;
        private double rating;
        private int numberOfReviews;

        // Mandatory fields in the builder constructor
        public ProductBuilder(String id, String name, double price) {
            this.id = generateRandomId();
            this.name = name;
            this.price = price;
        }

        // Setters for optional fields
        public ProductBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder setStockQuantity(int stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public ProductBuilder setCategory(Category category) {
            this.category = category;
            return this;
        }

        public ProductBuilder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public ProductBuilder setDiscountPrice(double discountPrice) {
            this.discountPrice = discountPrice;
            return this;
        }

        public ProductBuilder setWeight(double weight) {
            this.weight = weight;
            return this;
        }

        public ProductBuilder setDimensions(String dimensions) {
            this.dimensions = dimensions;
            return this;
        }

        public ProductBuilder setBrand(String brand) {
            this.brand = brand;
            return this;
        }

        public ProductBuilder setRating(double rating) {
            this.rating = rating;
            return this;
        }

        public ProductBuilder setNumberOfReviews(int numberOfReviews) {
            this.numberOfReviews = numberOfReviews;
            return this;
        }

        // Build method to create Product instance
        public Product build() {
            return new Product(this);
        }
    }
}
