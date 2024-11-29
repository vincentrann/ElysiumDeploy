package org.elysium.backend.repos;

import org.elysium.backend.models.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {
    // Find all credit cards for a specific user
    List<CreditCard> findByUserId(String userId);

    // Find the first credit card for a specific user (if only one is expected)
    Optional<CreditCard> findFirstByUserId(String userId);
}
