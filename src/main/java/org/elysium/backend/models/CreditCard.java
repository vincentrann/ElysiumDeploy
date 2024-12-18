package org.elysium.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.regex.Pattern;

@Entity
@Table(name = "CreditCards")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;

    @Column(name = "expiry_date")
    private String expiryDate; // It's now a String (MM/YY)

    private String cvv;

    private String cardHolderName;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference // Prevents recursion
    private User user;

    @Column(columnDefinition = "TEXT")
    private String billingAddress;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        if (expiryDate == null || !isValidExpiryDate(expiryDate)) {
            throw new IllegalArgumentException("Invalid expiry date format. Use MM/YY.");
        }
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    private boolean isValidExpiryDate(String expiryDate) {
        String pattern = "^(0[1-9]|1[0-2])/\\d{2}$"; // Matches MM/YY format
        return Pattern.matches(pattern, expiryDate);
    }
}
