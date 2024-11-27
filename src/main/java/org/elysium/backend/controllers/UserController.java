package org.elysium.backend.controllers;

import jakarta.servlet.http.HttpSession;
import org.elysium.backend.models.AdminUser;
import org.elysium.backend.models.User;
import org.elysium.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Optional;
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint to register a new user
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestParam String role, @RequestBody User user, HttpSession session) {
        User newUser = userService.register(role, user);

        // Store user details in the session
        session.setAttribute("userId", newUser.getId());
        session.setAttribute("userRole", role); // Use the role explicitly passed during registration

        return ResponseEntity.ok(newUser);
    }


    // Endpoint for user login
    /**@PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody User user, HttpSession session) {
        User loggedInUser = userService.login(user.getEmail(), user.getPassword());

        // Store user details in the session
        session.setAttribute("userId", loggedInUser.getId());
        session.setAttribute("userRole", loggedInUser instanceof AdminUser ? "Admin" : "Member");

        return ResponseEntity.ok(loggedInUser);
    }**/


    /**@PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Signed out successfully.");
    }**/

    // Endpoint to get all users
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        User user = userService.updateUser(id, updatedUser);
        return ResponseEntity.ok(user);
    }

    // Endpoint to get a user by ID
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }
}

