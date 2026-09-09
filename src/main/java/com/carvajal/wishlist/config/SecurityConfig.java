package com.carvajal.wishlist.config;

import com.carvajal.wishlist.security.CustomUserDetailsService;
import com.carvajal.wishlist.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración de seguridad con JWT Bearer tokens.
 * 
 * MECANISMO DE AUTENTICACIÓN:
 * - Usamos JWT Bearer tokens exclusivamente (NO HTTP Basic)
 * - Tokens obtenidos en /api/auth/register o /api/auth/login
 * - Cada request debe incluir: Authorization: Bearer <token>
 * - La validación ocurre en JwtAuthenticationFilter
 * 
 * AUTORIZACIÓN:
 * - Rutas públicas: /api/auth/**, GET /api/products/**, Swagger
 * - Rutas privadas: Requieren JWT válido en header Authorization
 * - UserRepository + CustomUserDetailsService cargan usuarios desde PostgreSQL
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * Cadena de filtros de seguridad.
     * 
     * Orden de reglas:
     * 1. Rutas públicas específicas (más restrictivas primero)
     * 2. Swagger/OpenAPI
     * 3. GET /api/products (lectura pública)
     * 4. Cualquier otro request requiere autenticación
     * 
     * El JwtAuthenticationFilter valida tokens antes de estas reglas.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (stateless + JWT no lo necesita)
                .csrf(csrf -> csrf.disable())
                
                // CORS: permitir requests desde frontend
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Sesiones stateless para JWT (sin session cookies)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Autorización HTTP
                .authorizeHttpRequests(auth -> auth
                        // 1. Rutas de autenticación (públicas)
                        .requestMatchers("/api/auth/**").permitAll()
                        
                        // 2. Lectura de productos (pública, solo GET)
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        
                        // 3. Documentación de API (pública)
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        
                        // 4. Cualquier otro request requiere autenticación JWT
                        .anyRequest().authenticated()
                )
                
                // Agregar filtro JWT ANTES del filtro por defecto
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configuración CORS para el frontend en desarrollo.
     * 
     * Importante: En producción, cambiar a los dominios reales.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Codificador de contraseñas con BCrypt.
     * 
     * Se usa para:
     * - Codificar valores en la BD durante registro
     * - Comparar en autenticación
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager para manejar la autenticación.
     * 
     * Usado en AuthService durante login para validar credenciales.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
