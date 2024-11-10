package org.elysium.backend.repos;

import org.elysium.backend.models.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    // Find all credit cards for a specific user
    List<CreditCard> findByUserId(String userId);
}
