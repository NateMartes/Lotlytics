package com.lotlytics.api.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.lotlytics.api.entites.token.UserToken;
import com.lotlytics.api.repositories.UserTokensRepository;

import java.security.Key;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The JwtService class defines service methods that are used
 * to create and decode JWT tokens.
 */
@Service
public class JwtService {

    public static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";
    private static final long EXPIRATION = 30;
    private UserTokensRepository userTokensRepository;

    public JwtService(UserTokensRepository userTokensRepository) {
        this.userTokensRepository = userTokensRepository;
    }

    /**
     * The generateToken method generates a JWT token from a username.
     * @param username
     * @return A String of a JWT token.
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * The createToken method creates a JWT token using a username.
     * @param claims empty
     * @param username
     * @return A String of a JWT token.
     */
    private String createToken(Map<String, Object> claims, String username) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime expiration = now.plusMinutes(EXPIRATION);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiration.toInstant()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * The getSignKey get a key that can be used to decode the JWT token.
     * @return a new signing key.
     */
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * The extractUsername method takes a JWT tokens and returns the username the token was made with.
     * @param token some JWT token.
     * @return a username.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * The extractExpiration method takes a JWT tokens and returns the expire time the token was given.
     * @param token some JWT token.
     * @return A timestamp of the JWT token.
     */
    public ZonedDateTime extractExpiration(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return ZonedDateTime.ofInstant(expiration.toInstant(), ZoneOffset.UTC);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * The isValidToken method checks the database if this token exists for this specifc user and is it
     * not expired.
     * 
     * @param token The token for the user.
     * @param username a user's username.
     * @return true if the token is valid.
     */
    private boolean isValidTokenInDatabase(String token, String username) {
        UserToken u = new UserToken();
        u.setToken(token);
        List<UserToken> matches = userTokensRepository.findAll(Example.of(u));

        if (matches.isEmpty()) {
            return false;
        }

        UserToken actualToken = matches.get(0);
        if (!extractUsername(actualToken.getToken()).equals(username)) {
            return false;
        }
        
        // Assume there will only be one match
        System.out.println("is actaul token expire time after the current time? " +  actualToken.getExpiresAt().isAfter(ZonedDateTime.now(ZoneOffset.UTC)));
        System.out.println("Current " +  ZonedDateTime.now(ZoneOffset.UTC));
        System.out.println("Expires at " +  actualToken.getExpiresAt());

        ZonedDateTime expireTime = actualToken.getExpiresAt();
        return expireTime.isAfter(ZonedDateTime.now(ZoneOffset.UTC));
    }

    /**
     * The isTokenExpired method determines if a token is expired.
     * This method calls to the database storing the tokens as a user could update the time in the token.
     * @param token a JWT token.
     * @param username a username.
     * @return true if the token is not expired.
     */
    private Boolean isTokenExpired(String token, String username) {
        return !isValidTokenInDatabase(token, username);
    }

    /**
     * The validateToken method determines if a token is a valid JWT token.
     * @param token a JWT token.
     * @param userDetails The details of the user this token pertains to.
     * @return true if the token is valid.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        System.out.println("Checking if token is valid: " + token);
        final String username = extractUsername(token);
        System.out.println("is token expire time before the current time? " +  extractExpiration(token).isBefore(ZonedDateTime.now(ZoneOffset.UTC)));
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, username));
    }
}