package org.elysium.backend.services;

import org.elysium.backend.models.CreditCard;
import org.elysium.backend.models.User;
import org.elysium.backend.repos.CreditCardRepository;
import org.elysium.backend.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;

    // Add a credit card for a specific user
    public CreditCard addCreditCard(String userId, CreditCard creditCard) {
        // Fetch the user from the database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Link the credit card to the user
        creditCard.setUser(user);

        return creditCardRepository.save(creditCard);
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
