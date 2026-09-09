package com.carvajal.wishlist.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret:}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private Key signingKey;

    /**
     * Valida e inicializa el secreto JWT al cargar la aplicación.
     * Lanza excepción si el secreto no está configurado correctamente.
     */
    @PostConstruct
    public void initializeSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException(
                "JWT_SECRET environment variable is required and must not be empty. " +
                "Please set the environment variable: export JWT_SECRET='your-secret-key-min-32-chars'"
            );
        }

        if (secret.length() < 32) {
            throw new IllegalArgumentException(
                "JWT_SECRET must be at least 32 characters long for HMAC-SHA256 signing. " +
                "Current length: " + secret.length() + " characters. " +
                "Example: export JWT_SECRET='$(openssl rand -base64 32)'"
            );
        }

        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Parsea y valida el token JWT en una sola operación.
     * @param token Token JWT a validar
     * @return Claims si el token es válido, null si no
     */
    public Claims getValidatedClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException | 
                 io.jsonwebtoken.UnsupportedJwtException | 
                 io.jsonwebtoken.MalformedJwtException | 
                 io.jsonwebtoken.security.SignatureException | 
                 IllegalArgumentException e) {
            // Token inválido, expirado, o con firma incorrecta
            return null;
        }
    }

    public String generateToken(UserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Valida un token JWT.
     * @deprecated Use {@link #getValidatedClaims(String)} instead
     */
    @Deprecated(forRemoval = true)
    public boolean validateToken(String token) {
        return getValidatedClaims(token) != null;
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getValidatedClaims(token);
        if (claims == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return claims.getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        Claims claims = getValidatedClaims(token);
        if (claims == null) {
            return List.of();
        }
        List<String> roles = claims.get("roles", List.class);
        return roles != null ? roles : List.of();
    }
}
