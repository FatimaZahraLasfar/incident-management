package org.fzl.incidentmanagement.config;
import org.fzl.incidentmanagement.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permet d'utiliser @PreAuthorize dans les contrôleurs (Section 9)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Désactiver CSRF (inutile pour une API REST stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configurer CORS pour React (Section 7.2)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 3. Autoriser l'accès à la console H2 (Section 7.1)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                // 4. Gestion des routes (Section 7)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/h2-console/**").permitAll() // Routes publiques
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Optionnel, géré aussi par @PreAuthorize
                        .requestMatchers("/api/**").authenticated() // Toutes les autres routes API nécessitent un JWT
                        .anyRequest().authenticated()
                )

                // 5. Mode Stateless : Pas de session HTTP côté serveur (Section 7.2)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 6. Déclarer le fournisseur d'authentification
                .authenticationProvider(authenticationProvider)

                // 7. Ajouter notre filtre JWT avant le filtre par défaut de Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuration CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // URL de React (Vite)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}