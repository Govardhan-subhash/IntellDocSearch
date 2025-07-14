package com.smartdocs.api_gateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            System.out.println("API-GATEWAY JWT SECRET: " + secret);

    }

    public Claims validateTokenAndGetClaims(String token) {
        System.out.println(">>> API Gateway: JwtUtil.validateTokenAndGetClaims called with token: " + token);
        try {
            logger.debug("Validating JWT token: {}", token);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("JWT validated successfully. Claims: {}", claims);
            System.out.println(">>> API Gateway: JWT validated successfully. Claims: " + claims);
            return claims;
        } catch (JwtException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            System.out.println(">>> API Gateway: JWT validation failed: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during JWT validation: {}", e.getMessage());
            System.out.println(">>> API Gateway: Unexpected error during JWT validation: " + e.getMessage());
            throw e;
        }
    }

    // Optional helper method to check token expiration
    public boolean isTokenExpired(Claims claims) {
        boolean expired = claims.getExpiration().before(new java.util.Date());
        logger.debug("Token expiration check: {} (expired={})", claims.getExpiration(), expired);
        return expired;
    }
}
