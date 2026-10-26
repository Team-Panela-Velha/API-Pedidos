package com.pedidos.api_pedidos.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import com.pedidos.api_pedidos.repository.UserRepository;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final TokenBlacklist blacklist;
    private final UserRepository users;

    public JwtFilter(JwtUtil jwtUtil, TokenBlacklist blacklist, UserRepository users) {
        this.jwtUtil = jwtUtil;
        this.blacklist = blacklist;
        this.users = users;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (!blacklist.isBlacklisted(token) && jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.extractClaims(token);
                String subject = claims.getSubject();
                String role = claims.get("role", String.class);

                if ("TABLE".equals(role)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            subject, token, List.of(new SimpleGrantedAuthority("ROLE_TABLE")));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else if (claims.get("id", Number.class) != null) {
                    Long id = claims.get("id", Number.class).longValue();
                    users.findByIdAndDeletedAtIsNull(id).filter(user -> !Boolean.FALSE.equals(user.getActive()))
                            .filter(user -> user.getEmail().equals(subject))
                            .ifPresent(user -> {
                                StaffUserDetails staff = StaffUserDetails.from(user);
                                SecurityContextHolder.getContext().setAuthentication(
                                        new UsernamePasswordAuthenticationToken(staff, token, staff.getAuthorities()));
                            });
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
