package com.example.FinTech.Wallet.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Date;


public final class JwtUtil {

    private static final  Key SECRET_KEY = Keys.hmacShaKeyFor(
            "THIS_IS_A_SECRET_KEY_FOR_JWT_GENERATION_123456".getBytes()
    );

    private static final long EXPIRATION_TIME = Duration.ofHours(1).toMillis();

    public static String generateToken(String username, String role) {

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public static  String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
}