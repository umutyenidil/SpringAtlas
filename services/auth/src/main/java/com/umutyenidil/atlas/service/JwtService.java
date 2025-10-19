package com.umutyenidil.atlas.service;

import com.umutyenidil.atlas.entity.Auth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.accessTokenExpiration}")
    private long accessTokenExpiration;
    @Value("${application.security.jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;
    @Value("${application.security.jwt.secret}")
    private String jwtSecret;

    public String generateToken(Auth auth) {
        return generateAccessToken(new HashMap<>(), auth);
    }

    public String generateAccessToken(Map<String, Object> claims, Auth auth) {
        return buildToken(claims, auth, accessTokenExpiration);
    }

    public String generateRefreshToken(Map<String, Object> claims, Auth auth) {
        return buildToken(claims, auth, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, Auth auth, long jwtExpiration) {
        var authorities = auth
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        final var issuedAt = Instant.now();
        final var expiredAt = issuedAt.plusMillis(jwtExpiration);

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(auth.getEmail())
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiredAt))
                .claim("authorities", authorities)
                .signWith(getSignKey())
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);

        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    private <Type> Type extractClaim(String token, Function<Claims, Type> claimsResolver) {
        final Claims claims = extractAllClaims(token);

        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
