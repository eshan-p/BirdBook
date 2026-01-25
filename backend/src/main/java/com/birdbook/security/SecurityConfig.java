package com.birdbook.security;

import java.util.List;

import com.birdbook.repository.UserDAO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // JWT = STATELESS
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

                .authorizeHttpRequests(auth -> auth
                    // Preflight
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // AUTH / PUBLIC - Login & Registration
                    .requestMatchers("/auth/**").permitAll()
                    
                    // STATIC RESOURCES - Images and profile pictures
                    .requestMatchers("/images/**").permitAll()
                    .requestMatchers("/profile_pictures/**").permitAll()
                    .requestMatchers("/backend_profile_pictures/**").permitAll()
                    
                    // GUEST ACCESS - Main feed (sightings), birds list (read-only)
                    .requestMatchers(HttpMethod.GET, "/sightings/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/birds/**").permitAll()

                    // AUTHENTICATED USERS ONLY - Everything else requires login
                    // AUTHENTICATED USERS ONLY
                    .requestMatchers("/groups/**").authenticated()
                    .requestMatchers("/search/**").authenticated()  
                    .requestMatchers("/users/**").authenticated()
                    .requestMatchers(HttpMethod.POST, "/sightings/**").hasAnyRole("BASIC_USER", "ADMIN_USER", "SUPER_USER")
                    .requestMatchers(HttpMethod.PUT, "/sightings/**").hasAnyRole("BASIC_USER", "ADMIN_USER", "SUPER_USER")
                    .requestMatchers(HttpMethod.PATCH, "/sightings/**").hasAnyRole("BASIC_USER", "ADMIN_USER", "SUPER_USER")
                    .requestMatchers(HttpMethod.DELETE, "/sightings/**").hasAnyRole("BASIC_USER", "ADMIN_USER", "SUPER_USER")
                    .requestMatchers(HttpMethod.POST, "/birds/**").hasAnyRole("BASIC_USER", "ADMIN_USER", "SUPER_USER")
                    .requestMatchers(HttpMethod.PUT, "/birds/**").hasAnyRole("BASIC_USER", "ADMIN_USER", "SUPER_USER")
                    .requestMatchers(HttpMethod.PATCH, "/birds/**").hasAnyRole("BASIC_USER", "ADMIN_USER", "SUPER_USER")
                    .requestMatchers(HttpMethod.DELETE, "/birds/**").hasAnyRole("BASIC_USER", "ADMIN_USER", "SUPER_USER")
                    
                    // ADMIN ROUTES
                    .requestMatchers("/admin/**").hasAnyRole("ADMIN_USER", "SUPER_USER")

                    // EVERYTHING ELSE
                    .anyRequest().authenticated()
                )

            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public UserDetailsService userDetailsService(UserDAO userDAO) {
        return username -> userDAO.findByUsername(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole().name())
                        .build()
                )
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username)
                );
    }
}
