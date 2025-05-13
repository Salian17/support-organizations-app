package com.example.supportorganizationsapp.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TokenProvider {

    private final SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JwtConstants.SECRET_KEY));
    private final JwtParser jwtParser;

    public TokenProvider() {
        this.jwtParser = Jwts.parser()
                .verifyWith(key)
                .build();
    }

    public String generateToken(Authentication authentication, long validity) {
        log.info("Generating token with validity: {} ms", validity);
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .issuer(JwtConstants.ISSUER)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + validity))
                .claim(JwtConstants.EMAIL, authentication.getName())
                .claim(JwtConstants.AUTHORITIES, authorities)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public Claims getClaimsFromToken(String jwt) {
        if (jwt.startsWith(JwtConstants.TOKEN_PREFIX)) {
            jwt = jwt.substring(JwtConstants.TOKEN_PREFIX.length());
        }
        return jwtParser.parseSignedClaims(jwt).getPayload();
    }

    public Claims validateToken(String token) {
        try {
            return jwtParser.parseSignedClaims(token).getPayload();
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            throw e;
        }
    }
}
