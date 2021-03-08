package mk.microservices.songsservice.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JwtTokenService {

    /**
     * The secret.
     */
    private final String secret;

    /**
     * Validity duration in seconds
     */
    private final long duration;

    public JwtTokenService(@Value("${jwt.secret}") String secret, @Value("${jwt.duration:3600}") long duration) {
        this.secret = secret;
        this.duration = duration;
    }

    private Instant calculateExpiry(Instant from) {
        return from.plus(duration, ChronoUnit.SECONDS);
    }

    public String validateTokenAndGetUserId(String tokenString) {
        try {
            final Jws<Claims> token = Jwts.parser().setSigningKey(this.secret).parseClaimsJws(tokenString);
            // User id is the "subject" claim of the token
            return token.getBody().getSubject();
        } catch (JwtException ex) {
            // We just got a bogus / invalid token
            return null;
        }
    }
}
