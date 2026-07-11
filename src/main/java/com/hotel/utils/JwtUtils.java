package com.hotel.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类 — 密钥和过期时间从 application.yml 读取
 */
@Component
public class JwtUtils {

    private static String secret;
    private static long expire;

    public JwtUtils(@Value("${jwt.secret}") String secret,
                    @Value("${jwt.expire}") long expire) {
        JwtUtils.secret = secret;
        JwtUtils.expire = expire;
    }

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateToken(Integer empId, String empName, String role) {
        return Jwts.builder()
                .claim("empId", empId)
                .claim("empName", empName)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(getKey())
                .compact();
    }

    public static long getRemainingTime(Claims claims) {
        long now = System.currentTimeMillis();
        long exp = claims.getExpiration().getTime();
        return exp - now;
    }

    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            return null;
        }
    }
}
