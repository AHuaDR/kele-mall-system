package com.my.utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 王建广
 * @version 1.0
 * @date 2025/12/2 14:27
 * @description JWT 工具类 用于生成和验证 Token
 */
@Component
public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);

    // JWT 荷载中存储用户名的 Key
    private static final String CLAIM_KEY_USERNAME = "sub";
    // JWT 荷载中存储创建时间的 Key
    private static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 根据用户信息生成 Token
     * 适配：B端传 AdminUserDetails，C端传 MemberUserDetails
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // 这里 userDetails.getUsername()：
        // - B端返回的是 admin 账号
        // - C端返回的是 138xxxx 手机号
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 根据用户名/手机号直接生成 Token (重载方法)
     * 方便登录接口直接调用，无需组装 UserDetails
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    /**
     * 从 Token 中获取用户名 (或手机号)
     */
    public String getUserNameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 验证 Token 是否有效
     * 1. Token 解析出的用户名与 UserDetails 是否一致
     * 2. Token 是否过期
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        String dbUsername = userDetails.getUsername();
        boolean isExpired = isTokenExpired(token);

        // ▼▼▼ 调试日志 (破案的关键) ▼▼▼
        System.out.println("========== Token 验证详情 ==========");
        System.out.println("Token里的用户名: " + username);
        System.out.println("数据库里的用户名: " + dbUsername);
        System.out.println("Token是否过期: " + isExpired);
        System.out.println("两者是否相等: " + username.equals(dbUsername));
        System.out.println("==================================");

        return username.equals(dbUsername) && !isExpired;
    }

    /**
     * 判断 Token 是否过期
     */
    private boolean isTokenExpired(String token) {
        Date expiredDate = getExpiredDateFromToken(token);
        return expiredDate.before(new Date());
    }

    /**
     * 从 Token 中获取过期时间
     */
    private Date getExpiredDateFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 核心方法：生成 Token
     */
    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, secret) // 使用 HS512 算法签名
                .compact();
    }

    /**
     * 核心方法：解析 Token 获取荷载信息
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            LOGGER.info("JWT格式验证失败: {}", token);
        }
        return claims;
    }

    /**
     * 生成过期时间
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }
}