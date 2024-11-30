package org.elysium.backend.services;

import org.elysium.backend.models.*;
import org.elysium.backend.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminCustomerService {
    @Autowired
    private UserRepository userRepository;

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean adminUpdateUser(String username, User updatedUser){
        return userRepository.findByUsername(username).map(user -> {
            user.setAddress(updatedUser.getAddress());
            user.setBillingAddress(updatedUser.getBillingAddress());
            user.setCreditCards(updatedUser.getCreditCards());
            user.setDob(updatedUser.getDob());
            user.setEmail(updatedUser.getEmail());
            user.setFirstName(updatedUser.getFirstName());
            user.setLastName(updatedUser.getLastName());
            user.setPassword(updatedUser.getPassword());
            user.setPhone(updatedUser.getPhone());
            return true;
        }).orElse(false);
    }
}
