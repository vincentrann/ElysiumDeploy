package org.elysium.backend.services;

import jakarta.transaction.Transactional;
import org.elysium.backend.models.CreditCard;
import org.elysium.backend.models.User;
import org.elysium.backend.repos.CreditCardRepository;
import org.elysium.backend.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;

    // Add a credit card for a specific user
    @Transactional
    public CreditCard addCreditCard(String userId, CreditCard creditCard) {
        // Fetch the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Validate and set the expiry date
        if (creditCard.getExpiryDate() == null || !isValidExpiryDate(creditCard.getExpiryDate())) {
            throw new IllegalArgumentException("Invalid expiry date format. Use MM/YY.");
        }

        // Link the credit card to the user
        creditCard.setUser(user);

        // Save the credit card
        return creditCardRepository.save(creditCard);
    }

    private boolean isValidExpiryDate(String expiryDate) {
        String pattern = "^(0[1-9]|1[0-2])/\\d{2}$"; // MM/YY format
        return expiryDate != null && expiryDate.matches(pattern);
    }



    // Get all credit cards for a specific user
    public List<CreditCard> getCreditCardsByUserId(String userId) {
        // Ensure the user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return creditCardRepository.findByUserId(userId);
    }

    // Update a credit card for a specific user
    public CreditCard updateCreditCard(String userId, Long cardId, CreditCard updatedCard) {
        // Fetch the credit card
        CreditCard existingCard = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Credit card not found with ID: " + cardId));

        // Check ownership
        if (!existingCard.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Credit card does not belong to this user.");
        }

        // Update credit card fields
        existingCard.setCardNumber(updatedCard.getCardNumber());
        existingCard.setExpiryDate(updatedCard.getExpiryDate());
        existingCard.setCvv(updatedCard.getCvv());
        existingCard.setCardHolderName(updatedCard.getCardHolderName());
        existingCard.setBillingAddress(updatedCard.getBillingAddress());

        return creditCardRepository.save(existingCard);
    }

    // Get the first credit card for a specific user
    public CreditCard getCreditCardById(Long creditCardId) {
        return creditCardRepository.findById(creditCardId)
                .orElseThrow(() -> new RuntimeException("Credit card not found with ID: " + creditCardId));
    }

    // Delete a credit card for a specific user
    public void deleteCreditCard(String userId, Long cardId) {
        // Fetch the credit card
        CreditCard existingCard = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Credit card not found with ID: " + cardId));

        // Check ownership
        if (!existingCard.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: Credit card does not belong to this user.");
        }

        creditCardRepository.deleteById(cardId);
    }
}
