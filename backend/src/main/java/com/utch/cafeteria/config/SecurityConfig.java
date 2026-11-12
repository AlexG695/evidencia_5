package com.utch.cafeteria.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuracion de Spring Security.
 *
 * Control de acceso por rol, derivado de los actores del Caso 4:
 *  - CLIENTE:  consulta el menu, registra pedidos y consulta sus pedidos.
 *  - PERSONAL: consulta pedidos y actualiza su estado (preparacion/entrega).
 *  - ADMIN:    gestiona el catalogo de productos.
 *
 * Se usa autenticacion HTTP Basic y sesion STATELESS porque el prototipo
 * expone una API REST consumida por un frontend Angular independiente.
 * CSRF se deshabilita por ser una API sin estado (no usa cookies de sesion).
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Consola H2 (evidencia de persistencia) y preflight CORS: abiertos.
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Identidad del usuario autenticado.
                .requestMatchers("/api/auth/me").authenticated()

                // Menu: cualquier usuario autenticado puede consultarlo.
                .requestMatchers(HttpMethod.GET, "/api/productos").authenticated()

                // Gestion de catalogo: solo ADMIN.
                .requestMatchers(HttpMethod.POST, "/api/productos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/productos/catalogo").hasRole("ADMIN")

                // Registrar pedido: solo CLIENTE.
                .requestMatchers(HttpMethod.POST, "/api/pedidos").hasRole("CLIENTE")

                // Avanzar estado del pedido: solo PERSONAL.
                .requestMatchers(HttpMethod.PATCH, "/api/pedidos/*/estado").hasRole("PERSONAL")

                // Consultar pedidos: cualquier rol autenticado.
                .requestMatchers(HttpMethod.GET, "/api/pedidos/**").authenticated()

                // Cualquier otra ruta requiere autenticacion.
                .anyRequest().authenticated()
            )
            // Necesario para que la consola H2 se muestre en un frame.
            .headers(h -> h.frameOptions(f -> f.disable()))
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    /** Las contrasenas se almacenan y verifican con BCrypt. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Permite que el frontend Angular (localhost:4200) consuma la API. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
