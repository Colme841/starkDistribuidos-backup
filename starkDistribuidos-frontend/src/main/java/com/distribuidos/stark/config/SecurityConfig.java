package com.distribuidos.stark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security para el frontend
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad HTTP
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                // Página de login y registro sin autenticación
                .requestMatchers("/", "/index.html", "/stark-security/", "/stark-security/index.html").permitAll()
                // Recursos estáticos sin autenticación
                .requestMatchers("/stark-security/static/**", "/static/**").permitAll()
                .requestMatchers("/stark-security/js/**", "/js/**").permitAll()
                .requestMatchers("/stark-security/styles/**", "/styles/**").permitAll()
                .requestMatchers("/stark-security/images/**", "/images/**").permitAll()
                .requestMatchers("/stark-security/css/**", "/css/**").permitAll()
                // WebSocket sin autenticación (será proxeado por gateway)
                .requestMatchers("/stark-security/ws/**", "/ws/**").permitAll()
                // API endpoints de autenticación
                .requestMatchers("/stark-security/api/auth/**", "/api/auth/**").permitAll()
                // Health check sin autenticación
                .requestMatchers("/actuator/health", "/health").permitAll()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {})
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.disable())
            );

        return http.build();
    }

    /**
     * Define usuarios en memoria para desarrollo
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin123"))
            .roles("ADMIN", "SECURITY", "USER")
            .build();

        UserDetails security = User.builder()
            .username("security")
            .password(passwordEncoder().encode("security123"))
            .roles("SECURITY", "USER")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user123"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, security, user);
    }

    /**
     * Configura el codificador de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

