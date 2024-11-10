package org.elysium.backend.controllers;

import org.elysium.backend.models.CreditCard;
import org.elysium.backend.services.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    // Add a credit card for a specific user
    @PostMapping("/user/{userId}")
    public CreditCard addCreditCard(@PathVariable String userId, @RequestBody CreditCard creditCard) {
        return creditCardService.addCreditCard(userId, creditCard);
    }

    // Get all credit cards for a user
    @GetMapping("/user/{userId}")
    public List<CreditCard> getCreditCardsByUser(@PathVariable String userId) {
        return creditCardService.getCreditCardsByUserId(userId);
    }

    // Update a user's credit card
    @PutMapping("/{cardId}/user/{userId}")
    public CreditCard updateCreditCard(@PathVariable Long cardId, @PathVariable String userId, @RequestBody CreditCard updatedCard) {
        return creditCardService.updateCreditCard(userId, cardId, updatedCard);
    }

    // Delete a user's credit card
    @DeleteMapping("/{cardId}/user/{userId}")
    public void deleteCreditCard(@PathVariable Long cardId, @PathVariable String userId) {
        creditCardService.deleteCreditCard(userId, cardId);
    }
}
