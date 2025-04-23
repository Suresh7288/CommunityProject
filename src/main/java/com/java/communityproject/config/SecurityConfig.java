package com.java.communityproject.config;

import com.java.communityproject.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.jaasapi.JaasApiIntegrationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private UserDetailsService userDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection

                .authorizeHttpRequests(auth->auth
                        .requestMatchers(
                                "/swagger-ui/**",       // Swagger UI HTML
                                "/v3/api-docs/**",      // Swagger API docs
                                "/swagger-resources/**" // Swagger resources
                        ).permitAll() // Allow access without authentication
                        //public endpoint
                        .requestMatchers("/api/auth/*").permitAll()
                        .requestMatchers("/api/payments/**").permitAll()// Allow public access to /api/auth/**
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/communities/**").permitAll()
                                .requestMatchers("/api/posts/**").permitAll()
                        //Role-based Access control
//                        .requestMatchers("/api/admin/*").hasRole("ADMIN")
//                        .requestMatchers("/api/moderator/*").hasRole("MODERATOR")
//                        .requestMatchers("/api/member/*").hasRole("MEMBER")
//                        .requestMatchers("/api/users/me").authenticated()
//                        .requestMatchers("/api/communities/**").authenticated()
                        // Allow all payment requests
                        // All other requests require authentication
                        .anyRequest().authenticated()
                ).sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Disable sessions
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password encoding
    }

}
