package io.spring.app.security.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Clock;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static io.spring.app.security.utils.Constants.*;
@Component
public class JwtTokenUtil {

    private Clock clock = DefaultClock.INSTANCE;

    private Long expiration = EXPIRATION;

    private String secret = SECRET;



    public String getUsernameFromToken(String token){
        return getClaimFromToken(token, Claims::getSubject);
    }

    private Date getExpirationDateFromToken(String token){
        return getClaimFromToken(token, Claims::getExpiration);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver){
        Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }


    private Claims getAllClaimsFromToken(String token){
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private boolean isTokenExpired(String token){
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(clock.now());
    }

    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());

    }

    private String doGenerateToken(Map<String, Object> claims, String subject){
        Date cratedDate = clock.now();
        Date expirationDate = calculateExpirationDate(cratedDate);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(cratedDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean canTokenBeRefreshed(String token){
        return !isTokenExpired(token);
    }

    public String refreshToken(String token){
        Date createdDate = clock.now();
        Date expirationDate = calculateExpirationDate(createdDate);

        Claims claims = getAllClaimsFromToken(token);

        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.ES512, secret)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String username = getUsernameFromToken(token);

        return Objects.equals(username, userDetails.getUsername()) && !isTokenExpired(token);
    }

    private Date calculateExpirationDate(Date cratedDate){
        return new Date(cratedDate.getTime() + expiration * 1000);
    }

}
