package com.example.TravelAgency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Esto es vital: nos permite usar @PreAuthorize en los controladores
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. ACTIVAMOS CORS A NIVEL DE SEGURIDAD (Busca el Bean de abajo)
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // Desactivamos CSRF porque somos una API REST
                .authorizeHttpRequests(auth -> auth
                        // 2. PERMITIMOS EL PREFLIGHT (El toque de puerta del navegador)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Dejamos público el catálogo para que la página principal funcione sin login
                        .requestMatchers(HttpMethod.GET, "/api/tour-packages/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/register").permitAll()

                        // Cualquier otra cosa (como el futuro carrito) exigirá estar autenticado
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
                );

        return http.build();
    }

    // =========================================================
    // CONFIGURACIÓN GLOBAL DE CORS (El permiso oficial para React)
    // =========================================================
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Le decimos explícitamente que acepte a Vite
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        // Le permitimos usar todos los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // Le permitimos enviar headers como el Token (Authorization) o JSON (Content-Type)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicamos esta regla a todas las rutas de nuestra API
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // =========================================================
    // TRADUCTOR DE ROLES: De Keycloak a Spring Security
    // =========================================================
    @Bean
    public JwtAuthenticationConverter jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Keycloak guarda los roles en un objeto llamado "realm_access"
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || !realmAccess.containsKey("roles")) {
                return List.of();
            }

            List<String> roles = (List<String>) realmAccess.get("roles");

            // Le agregamos el prefijo "ROLE_" que Java exige por defecto
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });
        return converter;
    }
}
