package com.universityweb.common.security;

import com.universityweb.common.Utils;
import com.universityweb.common.auth.entity.Token;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.JwtTokenCreationException;
import com.universityweb.common.auth.repos.TokenRepos;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Component
public class JwtGenerator implements Serializable {
    private static final Logger log = LogManager.getLogger(JwtGenerator.class);

    @Autowired
    private TokenRepos tokenRepos;

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    public String generateToken(
            String username,
            LocalDateTime current,
            LocalDateTime expiration
    ) {
        Date currentDate = Utils.toDate(current);
        Date expiresDate = Utils.toDate(expiration);

        try {
            Key key = getSignInKey();
            String token = Jwts.builder()
                    .setSubject(username)
                    .setIssuedAt(currentDate)
                    .setExpiration(expiresDate)
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
            log.info("New accessToken: {}", token);
            return token;
        } catch (Exception e) {
            throw new JwtTokenCreationException("Cannot create JWT tokenStr, error: " + e.getMessage());
        }
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = buildJwtParser()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public Date getExpiration(String token) {
        return buildJwtParser()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    private JwtParser buildJwtParser() {
        Key key = getSignInKey();
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Boolean isValidToken(String tokenStr) {
        if (!StringUtils.hasText(tokenStr) || !validateToken(tokenStr)) {
            return false;
        }

        Optional<Token> tokenOptional = tokenRepos.findByTokenStr(tokenStr);
        if (tokenOptional.isEmpty()) {
            return false;
        }

        Token token = tokenOptional.get();
        return !isTokenExpired(token);
    }

    public String generateAndSaveToken(@NonNull User user) {
        LocalDateTime curTime = LocalDateTime.now();
        LocalDateTime expirationTime = curTime.plus(SecurityUtils.EXPIRATION_DURATION_MILLIS, ChronoUnit.MILLIS);
        String generatedToken = generateToken(user.getUsername(), curTime, expirationTime);
        Token existingToken = user.getToken();
        Token newToken;
        if (existingToken != null) {
            existingToken.setTokenStr(generatedToken);
            existingToken.setExpiryDate(expirationTime);
            newToken = existingToken;
        } else {
            newToken = Token.builder()
                    .tokenStr(generatedToken)
                    .expiryDate(expirationTime)
                    .user(user)
                    .build();
        }
        Token saved = tokenRepos.save(newToken);
        return saved.getTokenStr();
    }

    private boolean isTokenExpired(Token token) {
        return token.getExpiryDate() != null
                && token.getExpiryDate().isBefore(LocalDateTime.now());
    }

    private boolean validateToken(String token) {
        try {
            buildJwtParser()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
