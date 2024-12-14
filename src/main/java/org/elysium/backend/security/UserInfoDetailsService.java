package org.elysium.backend.security;

import java.util.Optional;
import org.elysium.backend.models.User;

import org.elysium.backend.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserInfoDetailsService implements UserDetailsService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        //debugging - log user's dertails
        System.out.println("USERNAME GIVEN: " + username);
        System.out.println("User found: " + user.get().getUsername());

        return user.map(UserInfo::new).orElseThrow(()-> new UsernameNotFoundException("User was not found or does not exist"));
    }
    
}
