package com.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // 使用強安全性的密鑰
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // Token有效期為5分鐘
    private static final long EXPIRATION_TIME = 5 * 60 * 1000;

    private static final long EMAIL_VERIFICATION_EXPIRATION = 5 * 60 * 1000;
    
    // 生成重設密碼Token
    public String generatePasswordResetToken(String memberAccount) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(memberAccount)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // 從Token中獲取帳號
    public String getMemberAccountFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // 驗證Token
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
 // 生成Email驗證Token 
    public String generateEmailVerificationToken(String memberAccount) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EMAIL_VERIFICATION_EXPIRATION);

        return Jwts.builder()
                .setSubject(memberAccount)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
}