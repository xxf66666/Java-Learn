package com.learning.auth.security;

// jjwt 库的核心类
// Claims = JWT 的 payload 部分（业务字段）
import io.jsonwebtoken.Claims;
// Jwts 是构造 / 解析 JWT 的入口工具类
import io.jsonwebtoken.Jwts;
// Keys 帮你按算法要求生成 SecretKey
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 对称加密的密钥类
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT 工具类：生成 / 解析 token
 *
 * 用 @Component 让 Spring 管理这个 Bean
 */
@Component
public class JwtUtil {

    // SecretKey 是签名密钥；本服务持有就行，泄漏了别人能伪造 token
    // final 让密钥构造后不能再改
    private final SecretKey key;

    // 过期毫秒数
    private final long expireMs;

    /**
     * 构造器注入配置：
     *   @Value 把 yml/properties 里的值绑到参数上
     *   ":default" 是默认值（找不到配置时用）
     */
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expire-days}") int expireDays) {
        // 用密钥字符串的字节数组创建 HMAC-SHA 密钥
        // HMAC-SHA256 至少 32 字节，否则 jjwt 抛异常
        this.key = Keys.hmacShaKeyFor(secret.getBytes());

        // 配置的天数转毫秒
        this.expireMs = TimeUnit.DAYS.toMillis(expireDays);
    }

    /**
     * 生成 JWT
     *   subject: JWT 标准字段，存"主体"，这里放用户名
     *   claim: 自定义字段
     */
    public String generate(String username, Long userId, String role) {
        return Jwts.builder()
                .subject(username)                                            // sub 字段
                .claim("userId", userId)                                       // 自定义 userId
                .claim("role", role)                                            // 自定义 role
                .issuedAt(new Date())                                           // iat 签发时间
                .expiration(new Date(System.currentTimeMillis() + expireMs))    // exp 过期时间
                .signWith(key)                                                  // 用密钥签名
                .compact();                                                     // 输出最终的字符串
    }

    /**
     * 解析 JWT，签名错或过期会抛异常
     */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)                  // 校验签名用的密钥
                .build()
                .parseSignedClaims(token)         // 解析；过期 / 篡改 / 签名错会抛异常
                .getPayload();                    // 拿到 Claims（payload）
    }
}
