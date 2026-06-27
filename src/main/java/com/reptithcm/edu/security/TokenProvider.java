package com.reptithcm.edu.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenProvider {
    // secret
    @Value("${app.jwt.secret}")
    private String secret;
    // accessExpMs
    @Value("${app.jwt.access-expires-in-mili-seconds}")
    private Long accessExpMs;

    // secretKey + init()
    private SecretKey secretKey;
    @PostConstruct
    void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(Authentication authentication) {
        return generateAccessTokenFromUsername(authentication.getName());
    }

    public String generateAccessTokenFromUsername(String userName){
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessExpMs);

        return Jwts.builder()
                .subject(userName) // đối tượng mã hóa thành jwt
                .issuedAt(now)
                .expiration(exp)
                .signWith(secretKey) // loại ký tên/ loại chuyển sang jwt
                .compact();
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return false;
        }
    }

    public String getToken(HttpServletRequest request){
        String bearer =request.getHeader("authorization");
        return (StringUtils.hasText(bearer)) && bearer.startsWith("Bearer ") ? bearer.substring(7) : null;
    }

    public String getSubject(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();       // 0.12.x use getPayload() instead of getBody()
        return claims.getSubject();
    }
}
