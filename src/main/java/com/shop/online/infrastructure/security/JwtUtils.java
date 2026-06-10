package com.shop.online.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类 — Token 生成与解析
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration:7200}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration:604800}")
    private long refreshTokenExpiration;

    /**
     * 生成 Access Token（用户）
     */
    public String generateAccessToken(Long userId, String username) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("username", username)
                .withClaim("role", "ROLE_USER")
                .withClaim("type", "access")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 生成管理员 Access Token
     */
    public String generateAdminAccessToken(Long adminId, String username) {
        return JWT.create()
                .withSubject(String.valueOf(adminId))
                .withClaim("username", username)
                .withClaim("role", "ROLE_ADMIN")
                .withClaim("type", "access")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 从 Token 中获取角色
     */
    public String getRole(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getClaim("role").asString();
    }

    /**
     * 生成 Refresh Token
     */
    public String generateRefreshToken(Long userId, String username) {
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withClaim("username", username)
                .withClaim("type", "refresh")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiration * 1000))
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 校验 Token 并返回解析结果
     */
    public DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
        } catch (TokenExpiredException e) {
            log.warn("Token已过期: {}", e.getMessage());
            throw e;
        } catch (JWTVerificationException e) {
            log.warn("Token校验失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 从 Token 中获取用户 ID
     */
    public Long getUserId(String token) {
        DecodedJWT jwt = verifyToken(token);
        return Long.valueOf(jwt.getSubject());
    }

    /**
     * 从 Token 中获取用户名
     */
    public String getUsername(String token) {
        DecodedJWT jwt = verifyToken(token);
        return jwt.getClaim("username").asString();
    }

    /**
     * 判断 Token 是否已过期
     */
    public boolean isTokenExpired(String token) {
        try {
            DecodedJWT jwt = verifyToken(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (TokenExpiredException e) {
            return true;
        } catch (Exception e) {
            return true;
        }
    }
}
