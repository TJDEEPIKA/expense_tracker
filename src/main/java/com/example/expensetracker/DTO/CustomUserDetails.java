package com.example.expensetracker.DTO;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails extends User {

    private int clientId;
    private boolean isAdmin;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, int clientId, boolean isAdmin) {
        super(username, password, authorities);
        this.clientId = clientId;
        this.isAdmin = isAdmin;
    }

    public int getClientId() {
        return clientId;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
