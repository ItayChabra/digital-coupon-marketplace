package com.nexus.marketplace.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


// Reads the Authorization: Bearer <token> header and grants the appropriate role.
//
// ROLE_ADMIN -> admin token -> access to /api/admin/**
// ROLE_RESELLER -> reseller token -> access to /api/v1/**
//
// If no valid token is present, no authentication is set: Spring Security
// will return 401 on protected endpoints automatically.

@Component
public class BearerTokenFilter extends OncePerRequestFilter {

    private final String adminToken;
    private final String resellerToken;

    public BearerTokenFilter(
            @Value("${app.security.admin-token}") String adminToken,
            @Value("${app.security.reseller-token}") String resellerToken) {
        this.adminToken = adminToken;
        this.resellerToken = resellerToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (token.equals(adminToken)) {
                setAuthentication("ROLE_ADMIN");
            } else if (token.equals(resellerToken)) {
                setAuthentication("ROLE_RESELLER");
            }
            // Unknown token -> no auth set -> Spring Security enforces 401
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                role, null, List.of(new SimpleGrantedAuthority(role))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
