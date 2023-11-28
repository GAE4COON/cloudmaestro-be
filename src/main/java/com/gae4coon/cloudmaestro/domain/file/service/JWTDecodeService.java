package com.gae4coon.cloudmaestro.domain.file.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class JWTDecodeService {

    @Value("${spring.jwt.secretKey}")
    private String secretKey;

    public HashMap<String, Object> decodeJWT(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey) // Replace with your signing key
                    .parseClaimsJws(token)
                    .getBody();

            return new HashMap<>(claims);
        }
        return null;
    }
}

