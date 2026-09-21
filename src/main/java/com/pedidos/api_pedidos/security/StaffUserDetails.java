package com.pedidos.api_pedidos.security;

import com.pedidos.api_pedidos.domain.entity.UserEntity;
import com.pedidos.api_pedidos.domain.enums.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record StaffUserDetails(Long id, String email, String passwordHash, UserRole role) implements UserDetails {

    public static StaffUserDetails from(UserEntity user) {
        return new StaffUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(), user.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
