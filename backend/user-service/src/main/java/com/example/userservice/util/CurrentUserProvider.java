package com.example.userservice.util;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class CurrentUserProvider {
    public Optional<String> getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return Optional.ofNullable((String) auth.getPrincipal());
        }
        return Optional.empty();
    }

    public Optional<String> getUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object details = auth.getDetails();
            return Optional.ofNullable(details != null ? details.toString() : null);
        }
        return Optional.empty();
    }

    public String getStringUserName(){
        return getUsername().stream().findFirst().toString();
    }
    public Optional<String> getRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getAuthorities().isEmpty()) {
            GrantedAuthority authority = auth.getAuthorities().iterator().next();
            return Optional.ofNullable(authority.getAuthority());
        }
        return Optional.empty();
    }
}