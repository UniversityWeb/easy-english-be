package com.universityweb.common.auth.util;

import com.universityweb.common.Utils;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.JwtTokenCreationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class JwtTokenUtil {
    public static final int EXPIRATION_DURATION_MILLIS = 24 * 60 * 60 * 1000;

    private static final String SECRET_KEY = "TaqlmGv1iEDMRiFp/pHuID1+T84IABfuA0xXh4GhiUI=";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_PHONE_NUMBER = "phoneNumber";
    private static final String CLAIM_EMAIL = "email";

    public static String generateToken(User user, LocalDateTime expiresAt) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USERNAME, user.getUsername());
        claims.put(CLAIM_PHONE_NUMBER, user.getPhoneNumber());
        claims.put(CLAIM_EMAIL, user.getEmail());
        try {
            Date expirationDate = Utils.toDate(expiresAt);
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(expirationDate)
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
            throw new JwtTokenCreationException("Cannot create JWT tokenStr, error: " + e.getMessage());
        }
    }

    public static boolean isTokenExpired(String token) {
        Date expirationDate = extractClaim(token, Claims::getExpiration);
        return expirationDate.before(new Date());
    }

    public static  <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get(CLAIM_USERNAME, String.class));
    }

    private static Key getSignKey() {
        byte[] bytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

    private static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
