package org.elysium.backend.services;


import org.elysium.backend.models.User;
import org.elysium.backend.models.UserFactory;
import org.elysium.backend.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    /**
     * Registers a new user based on their role.
     *
     * @param role The role of the user (e.g., "admin", "member").
     * @param user The user details.
     * @return The registered user.
     */
    public User register(String role, User user) {
        // Check if the user already exists by email
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        // Generate a random ID if not set
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(user.generateRandomId());
        }

        // Create the user with the appropriate role using the UserFactory
        User newUser = UserFactory.createUser(role);
        newUser.setId(user.getId());
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword()); // Save plaintext password *changed to BCrypt
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setPhone(user.getPhone());
        newUser.setAddress(user.getAddress());
        newUser.setBillingAddress(user.getBillingAddress());
        newUser.setDob(user.getDob());
        newUser.setEmailVerified(user.isEmailVerified());

        // Save the user to the database
        return userRepository.save(newUser);
    }

    /**
     * Authenticates a user using email and password.
     *
     * @param email    The user's email.
     * @param password The user's password.
     * @return The authenticated user.
     */
    public User login(String email, String password) {
        // Find the user by email
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userOptional.get();

        // Verify the provided password against the stored plaintext password
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }

        return user;
    }

    public User updateUser(String id, User updatedUser) {
        // Find the user by ID
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + id);
        }

        User user = existingUser.get();

        // Update user fields
        user.setUsername(updatedUser.getUsername());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());
        user.setBillingAddress(updatedUser.getBillingAddress());
        user.setDob(updatedUser.getDob());
        user.setEmailVerified(updatedUser.isEmailVerified());

        // Save updated user
        return userRepository.save(user);
    }

    /**
     * Retrieves all users.
     *
     * @return A list of all users.
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id The user ID.
     * @return An Optional containing the user if found.
     */
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }

    /**
     * Creates a new user (for admin use).
     *
     * @param user The user to create.
     * @return The created user.
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id The user ID.
     */
    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }

}