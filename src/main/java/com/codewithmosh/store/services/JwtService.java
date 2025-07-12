package com.codewithmosh.store.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// This class is used to generate web token

@Service
public class JwtService {
    @Value("${spring.jwt.secret}")
    private String secret;

public String generateToken(String emial) {
    final long tokenExpiration = 86400; // the number of seconds in one day

    return Jwts.builder()
            .subject(emial)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
            .compact();
}
}
