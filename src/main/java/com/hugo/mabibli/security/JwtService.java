package com.hugo.mabibli.security;

import com.hugo.mabibli.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtService {

private final JwtProperties properties;
private final SecretKey signingKey;

public JwtService(JwtProperties properties) {
    this.properties = properties;

    byte[] decodedSecret = Decoders.BASE64.decode(properties.secret());

    if (decodedSecret.length < 32) {
        throw new IllegalStateException("JWT_SECRET doit contenir au moins 256 bits");
    }

    this.signingKey = Keys.hmacShaKeyFor(decodedSecret);
}
    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant expiration = now.plus(properties.expiration());

        return Jwts.builder()
                .subject(username)
                .issuer(properties.issuer())
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = parseClaims(token);

        return claims.getSubject().equals(userDetails.getUsername())
                && claims.getIssuer().equals(properties.issuer())
                && claims.getExpiration().after(new Date());
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(properties.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}