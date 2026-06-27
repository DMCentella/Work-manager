package com.microservicios.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JwtUtil {

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(
            "mCjGjYrN4Bz6DfFgHkLmNpQrStUwXyZaAbCdEfGhIjKlMnOpQrStUvWxYz0123456789"
                    .getBytes(StandardCharsets.UTF_8));
    private static final long EXPIRATION_MS = 8 * 60 * 60 * 1000; // 8 horas

    public static String generateToken(String username, List<String> roles, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .addClaims(extraClaims)
                .signWith(SECRET_KEY)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Key getSigningKey() {
        return SECRET_KEY;
    }
}
