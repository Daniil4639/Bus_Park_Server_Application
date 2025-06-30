package app.security.jwt;

import app.security.user_details.BusUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    public String generateToken(UserDetails details) {
        Map<String, Object> claims = new HashMap<>();
        if (details instanceof BusUserDetails customUserDetails) {
            claims.put("username", customUserDetails.getUsername());
            claims.put("password", customUserDetails.getPassword());
            claims.put("roles", customUserDetails.getAuthorities());
        }

        return generate(claims, details);
    }

    public String extractUsername(String token) throws SecurityException {
        return Jwts.parser().setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public Date extractExpiration(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).build()
                .parseClaimsJws(token).getBody().getExpiration();
    }

    public boolean isTokenValid(String token, UserDetails details) throws SignatureException {
        String username = extractUsername(token);
        boolean isTokenExpired = extractExpiration(token).before(new Date());

        return (username.equals(details.getUsername())) && !isTokenExpired;
    }

    private String generate(Map<String, Object> claims, UserDetails details) {
        return Jwts.builder().setClaims(claims).setSubject(details.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS512).compact();
    }
}