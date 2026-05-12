package com.petstore.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Custom UserDetailsService implementation for Spring Security.
 * 
 * Loads user details from the database and creates UserDetails objects
 * for Spring Security authentication.
 * 
 * @since 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired(required = false)
    private com.petstore.repository.UserRepository userRepository;

    /**
     * Load user by username/email.
     * 
     * @param username User email or ID
     * @return UserDetails for Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // For Phase 1, return a basic user until repositories are implemented
        // In Phase 4, this will query the UserRepository

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        return User.builder()
                .username(username)
                .password("$2a$10$N9qo8uLOickgx2ZMRZoMye") // Placeholder bcrypt hash
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
