package com.hui.util;

import com.hui.entity.User;
import com.hui.service.UserService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationInMs;
    
    @Autowired
    private UserService userService;

    public String createToken(Object data){
        // 當前時間
        long currentTime = System.currentTimeMillis();
        // 過期時間
        long expTime = currentTime+jwtExpirationInMs;
        // 構建jwt
        JwtBuilder builder = Jwts.builder()
                .setId(UUID.randomUUID()+"")
                .setSubject(JSON.toJSONString(data))
                .setIssuer("system")
                .setIssuedAt(new Date(currentTime))
                .signWith(SignatureAlgorithm.HS256, encodeSecret(jwtSecret))
                .setExpiration(new Date(expTime));
                
        return builder.compact();
    }
    
    /**
     * 從 Authentication 對象創建 JWT Token
     */
    public String createToken(Authentication authentication) {
        // 從 Authentication 中獲取用戶信息
        String username = authentication.getName();

        // 創建自定義的數據map
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        // 使用現有方法創建token
        return createToken(userData);
    }
    
    /**
     * 為指定郵箱創建 JWT Token
     */
    public String createToken(String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", email);

        log.info("Creating token for user: {}", email);
        return createToken(userData);
    }

    public static void main(String[] args) {
        JwtTokenProvider jwtUtil = new JwtTokenProvider();
        System.out.println(jwtUtil.createToken("test"));
    }

    private Key encodeSecret(String key){
        // 使用與簽名算法匹配的密鑰規範（HS256 = HMAC-SHA256）
        byte[] keyBytes = key.getBytes();
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    //claims一種用於jwt的類型
    public Claims parseToken(String token) throws Exception {
        return Jwts
                .parserBuilder()
                .setSigningKey(encodeSecret(jwtSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T parseToken(String token,Class<T> clazz) throws Exception {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(encodeSecret(jwtSecret))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return JSON.parseObject(body.getSubject(),clazz);
    }

    public Integer getUserIdFromToken(String token) {
        try {
            Claims claims = parseToken(token);
            // 從Claims中獲取用戶信息
            JSONObject subClaim = JSON.parseObject(claims.getSubject());
            
            // 先嘗試從username中獲取email
            String username = subClaim.getString("username");
            if (username != null) {
                // 通過email查詢數據庫獲取用戶ID
                User user = userService.getUserByEmail(username);
                if (user != null) {
                    return user.getId().intValue();
                }
            }
            
            // 如果沒有username字段，嘗試獲取uid字段
            Integer uid = subClaim.getInteger("uid");
            return uid;
        } catch (Exception e) {
            // 在解析過程中出現異常，返回null或拋出自定義異常
            return null;
        }
    }
}