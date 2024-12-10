package org.elysium.backend.security;

import java.util.Collection;
import java.util.List;

import org.elysium.backend.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserInfo implements UserDetails{
    private String username;
    private String pass;
    private List<GrantedAuthority> roles;

    public UserInfo(User user){
        this.username = user.getUsername();
        this.pass = user.getPassword();
        this.roles = List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole()));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    @Override
    public String getPassword() {
        return this.pass;
    }

    @Override
    public String getUsername() {
        return this.username;
    }
    
}
