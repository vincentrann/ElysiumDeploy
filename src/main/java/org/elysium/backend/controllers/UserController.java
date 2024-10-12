package org.elysium.backend.controllers;

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
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.register(user);
        return ResponseEntity.ok(newUser);
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestParam String email, @RequestParam String password) {
        User loggedInUser = userService.login(email, password);
        return ResponseEntity.ok(loggedInUser);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUserById(id);
    }
}

