package com.project.judging.Config.Jwt;

import com.project.judging.Config.UserDetails.UserDetailsImpl;
import com.project.judging.Config.UserDetails.UserDetailsServiceImpl;
import com.project.judging.Exception.CustomException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static javax.crypto.Cipher.SECRET_KEY;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private long expiration;

    public JwtUtils(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

//    private Key key() {
//        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
//    }

    private Key key() {
        byte[] bytes = Base64.getDecoder()
                .decode(secret);
        return new SecretKeySpec(bytes, "HmacSHA256"); }

    public String generateToken(UserDetailsImpl userDetails) {
        JwtBuilder builder = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expiration))
                .signWith(key(), SignatureAlgorithm.HS512);

        if (userDetails.getSemesterId() != null) {
            builder.claim("semesterId", userDetails.getSemesterId());
        }

        return builder.compact();
    }

    public String removeBearerTokenFormat(String token) {
        return token.replace("Bearer ", "");

    }
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public Integer getSemesterIdFromToken(String token) {
        if (token == null || token.isEmpty()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Token cannot be null or empty");
        }

        // Decode the token and extract the semesterId
        Claims claims = Jwts.parser()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return (Integer) claims.get("semesterId");
    }

    public boolean validateJwtToken(String authToken) {
        if (authToken == null) {
            logger.error("Invalid JWT token: Token is null");
            return false;
        } else if (authToken.isEmpty()) {
            logger.error("Invalid JWT token: empty");
            return false;
        } else if ("{{}}".equals(authToken)) {
            logger.error("Invalid JWT token: malformed");
            return false;
        }
        try {
            Jwts.parser().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromJwtToken(token);
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}
