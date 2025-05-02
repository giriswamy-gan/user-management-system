package com.apica.user_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration-ms}") private long expMs;

    /**
     * Generate token by user ID.
     *
     * @param ud       UserDetails
     * @return String
     */
    public String generateToken(UserDetails ud) {
        Map<String,Object> claims = new HashMap<>();
        claims.put("roles", ud.getAuthorities());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(ud.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Parsing token.
     *
     * @param token String jwt token to parse
     * @return Claims object
     */
    public Claims parseToken(final String token) {
        try {
            return Jwts.parser().verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            System.err.println("Error parsing JWT token: " + e.getMessage());
            // throw new RuntimeException("Failed to parse JWT token", e);
            return null;
        }
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Get expire date.
     *
     * @return Date object
     */
    private Date getExpireDate(final Long expires) {
        return new Date(new Date().getTime() + expires);
    }


    /**
     * Check token is expired or not.
     *
     * @param token String jwt token to get expiration date
     * @return True or False
     */
    private boolean isTokenExpired(final String token) {
        try {
            Date expirationDate = parseToken(token).getExpiration();
            return expirationDate.before(new Date());
        } catch (Exception e) {
            System.err.println("Failed to check token expiration: " + e.getMessage());
            return true;
        }
    }


    /**
     * Boolean result of whether token is valid or not.
     *
     * @param token String token
     * @return boolean
     */
    public boolean validateToken(final String token) {
        try {
            parseToken(token);

            return !isTokenExpired(token);
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
            return false;
        }
    }




//    public Claims parseToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//}
}
