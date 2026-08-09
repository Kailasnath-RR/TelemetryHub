package com.kailas.TelemetryHub.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key getSigingKey(){
        byte[] bytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(bytes);
    }
    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) getSigingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public String generateToken(UserDetails userDetails){
        return Jwts.builder().
                subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+1000L*60*30))
                .signWith(getSigingKey())
                .compact();
    }

    public String extractUsername(String token){
        return extractAllClaims(token).getSubject();
    }
    public Date extractExpiration(String token){
        return extractAllClaims(token).getExpiration();
    }
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public boolean isTokenValid(String token,UserDetails userDetails){
        return !isTokenExpired(token) && (
                extractUsername(token)
                        .equals(userDetails.getUsername()));
    }
}
