package com.nexus.marketplace.config;

import com.nexus.marketplace.security.BearerTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final BearerTokenFilter bearerTokenFilter;

    public SecurityConfig(BearerTokenFilter bearerTokenFilter) {
        this.bearerTokenFilter = bearerTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Admin endpoints — require ROLE_ADMIN
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Reseller endpoints — require ROLE_RESELLER
                        .requestMatchers("/api/v1/**").hasRole("RESELLER")
                        // Customer + frontend — fully public
                        .requestMatchers("/api/customer/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/**/*.js", "/**/*.css").permitAll()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
