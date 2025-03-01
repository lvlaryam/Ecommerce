package com.app.ecommerce.configuration.security.jwt;

import com.app.ecommerce.configuration.exception.SystemServiceException;
import com.app.ecommerce.configuration.exception.constant.ExceptionMessages;
import com.app.ecommerce.configuration.security.constant.SecurityConstants;
import com.app.ecommerce.core.user.User;
import com.app.ecommerce.core.user.utils.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;

@Component
public class JwtService {

    Logger logger = Logger.getLogger(this.getClass().getName());


    private String generateToken(Map<String, Object> claims, String subject, SecretKey key, Long expirationDuration) {

        return Jwts.builder()
                .setIssuer("soberDriver")
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expirationDuration))
                .signWith(key).compact();
    }

    public String generateAccessToken(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.ACCESS_KEY.getBytes(StandardCharsets.UTF_8));
        return generateToken(claims, "accessToken", key, SecurityConstants.ACCESS_TOKEN_DURATION);
    }

    public String generateRefreshToken(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.REFRESH_KEY.getBytes(StandardCharsets.UTF_8));
        return generateToken(claims, "refreshToken", key, SecurityConstants.REFRESH_TOKEN_DURATION);
    }

    public Map<String, Object> generateClaims(User user, UserRole userRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userRole);
        return claims;
    }

    public Claims getClaimsFromToken(String token, SecretKey key) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String userNameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.ACCESS_KEY.getBytes(StandardCharsets.UTF_8));
        Claims claims = getClaimsFromToken(token, key);
        return (String) claims.get("username");
    }

    public String authoritiesFromToken(String accessToken) {
        SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.ACCESS_KEY.getBytes(StandardCharsets.UTF_8));
        Claims claims = getClaimsFromToken(accessToken, key);
        return claims.get("authorities").toString();
    }

    public Claims refreshTokenClaims(String refreshToken) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SecurityConstants.REFRESH_KEY.getBytes(StandardCharsets.UTF_8));
            return getClaimsFromToken(refreshToken, key);
        } catch (Exception ex) {
            throw new SystemServiceException(ExceptionMessages.INVALID_REFRESH_TOKEN);
        }
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
        Set<String> authoritiesSet;
        authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority : collection) {
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }
}
