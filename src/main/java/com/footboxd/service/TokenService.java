package com.footboxd.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String gerarToken(String login) {
        Date agora = new Date();
        Date expira = new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(login)
                .issuedAt(agora)
                .expiration(expira)
                .signWith(key())
                .compact();
    }

    public String getLoginDoToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            getLoginDoToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}