package com.shopsphere.api.config;

import com.shopsphere.api.security.CustomUserDetailsService;
import com.shopsphere.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final CustomUserDetailsService userDetailsService;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(auth -> auth
                                                // Public Endpoints
                                                .requestMatchers("/api/auth/**").permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/products/**")
                                                .permitAll()
                                                .requestMatchers("/api/uploads/**").permitAll()
                                                .requestMatchers("/api/v1/uploads/**").permitAll()
                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                                                "/swagger-ui.html")
                                                .permitAll()

                                                // Admin-only Endpoints (Product Mutations)
                                                .requestMatchers(org.springframework.http.HttpMethod.POST,
                                                                "/api/products",
                                                                "/api/products/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(org.springframework.http.HttpMethod.PUT,
                                                                "/api/products",
                                                                "/api/products/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(org.springframework.http.HttpMethod.PATCH,
                                                                "/api/products",
                                                                "/api/products/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers(org.springframework.http.HttpMethod.DELETE,
                                                                "/api/products",
                                                                "/api/products/**")
                                                .hasRole("ADMIN")

                                                // Admin Dashboard
                                                .requestMatchers("/api/dashboard/**").hasRole("ADMIN")

                                                // Inventory (Direct Access)
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/inventory/**")
                                                .authenticated()
                                                .requestMatchers("/api/inventory/**").hasRole("ADMIN")

                                                // Users
                                                .requestMatchers("/api/users/**").authenticated()

                                                // Orders Access Control
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/orders")
                                                .authenticated()
                                                // Allow Status Updates for Authenticated Users (Customer Cancel)
                                                .requestMatchers(org.springframework.http.HttpMethod.PUT,
                                                                "/api/orders/*/status")
                                                .authenticated()
                                                .requestMatchers(org.springframework.http.HttpMethod.PUT,
                                                                "/api/orders/*/cancel")
                                                .authenticated()

                                                // Admin-only Order Mutations (General Update)
                                                .requestMatchers(org.springframework.http.HttpMethod.PUT,
                                                                "/api/orders/**")
                                                .hasRole("ADMIN")

                                                .requestMatchers(org.springframework.http.HttpMethod.PATCH,
                                                                "/api/orders/**")
                                                .authenticated()

                                                // Protected Endpoints (User specific orders, profile, etc.)
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(
                                                                org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(jwtAuthFilter,
                                                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
                org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
                configuration.setAllowedOrigins(java.util.List.of("http://localhost:4200")); // Allow Frontend
                configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                configuration.setAllowedHeaders(java.util.List.of("Authorization", "Content-Type"));
                configuration.setAllowCredentials(true);
                org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsService);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }
}
