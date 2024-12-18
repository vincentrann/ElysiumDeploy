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

    public User adminUpdateUser(String username, User updatedUser){
        User user = userRepository.findByUsername(username).get();
        if (updatedUser.getAddress() != null) {
            user.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getCreditCards() != null){
            user.setCreditCards(updatedUser.getCreditCards());
        }
        if (updatedUser.getBillingAddress() != null) {
            user.setBillingAddress(updatedUser.getBillingAddress());
        }
        if (updatedUser.getDob() != null) {
            user.setDob(updatedUser.getDob());
        }
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().isEmpty()) {
            user.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null && !updatedUser.getLastName().isEmpty()) {
            user.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword());
        }
        if (updatedUser.getPhone() != null && !updatedUser.getPhone().isEmpty()) {
            user.setPhone(updatedUser.getPhone());
        }

        return userRepository.save(user);
    }
}
