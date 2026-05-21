package com.learning.erp.framework.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT 工具类（ERP 项目版）
 * 比 Week 8 的版本简化：只放 userId + username，权限由 LoginUserLoader 实时查
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expireMs;

    public JwtUtil(@Value("${jwt.secret:change-this-secret-key-min-32-chars-long!}") String secret,
                   @Value("${jwt.expire-days:7}") int expireDays) {
        // HMAC-SHA 至少 32 字节
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expireMs = TimeUnit.DAYS.toMillis(expireDays);
    }

    /** 生成 JWT，只放 userId + username 到 payload */
    public String generate(Long userId, String username) {
        return Jwts.builder()
                .subject(username)                    // sub 字段
                .claim("userId", userId)               // 自定义字段
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireMs))
                .signWith(key)
                .compact();
    }

    /** 解析；过期 / 篡改抛异常 */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
