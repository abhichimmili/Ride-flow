package com.vcube.rideflow.security;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long jwtExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long jwtExpiration) {

        byte[] keyBytes = Decoders.BASE64.decode(secret);

        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtExpiration = jwtExpiration;
    }

    // Generate JWT
    public String generateToken(UserDetails userDetails) {

        Date now = new Date();

        Date expiryDate =
                new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    // Extract username from JWT
    public String extractUsername(String token) {

        return extractAllClaims(token).getSubject();
    }

    // Validate JWT
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername());
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}