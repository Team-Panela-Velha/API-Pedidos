package com.pedidos.api_pedidos.security;

import com.pedidos.api_pedidos.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class StaffUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public StaffUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return users.findByEmailAndDeletedAtIsNull(email)
                .filter(user -> !Boolean.FALSE.equals(user.getActive()))
                .map(StaffUserDetails::from)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado ou inativo"));
    }
}
