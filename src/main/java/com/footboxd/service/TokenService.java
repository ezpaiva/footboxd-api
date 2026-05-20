package com.footboxd.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço de geração e validação de tokens JWT.
 * Mantém compatibilidade com tokens já emitidos.
 */
@Service
@Slf4j
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Gera um novo token JWT válido por expirationMs millisegundos
     */
    public String gerarToken(String login) {
        Date agora = new Date();
        Date expira = new Date(agora.getTime() + expirationMs);

        String token = Jwts.builder()
                .subject(login)
                .issuedAt(agora)
                .expiration(expira)
                .signWith(key())
                .compact();
        
        log.debug("Token gerado para login: {}", login);
        return token;
    }

    /**
     * Extrai o login (subject) do token JWT
     * @throws JwtException se o token for inválido
     */
    public String getLoginDoToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (JwtException ex) {
            log.warn("Erro ao extrair login do token: {}", ex.getMessage());
            throw ex;
        }
    }

    /**
     * Valida se um token é válido e não expirou
     */
    public boolean tokenValido(String token) {
        try {
            getLoginDoToken(token);
            return true;
        } catch (JwtException ex) {
            log.warn("Token inválido ou expirado");
            return false;
        } catch (Exception ex) {
            log.error("Erro inesperado na validação do token", ex);
            return false;
        }
    }
}