package mk.microservices.songsservice.controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import mk.microservices.songsservice.model.User;
import mk.microservices.songsservice.services.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

public class JwtServiceTest {

    public static final String SECRET = "7VOfkWnIapAjJ/DiTm1h1B3wJWOOkZgUZPm+StLJ2QWkOx+7oaM0xpDI3xtiAp9PZ5qzajbFMXnuvmojDNRd6Oa9sllFi7vmMrtAWEObuDSG2cJDscpd42evQbdWaZQ7876wzqBzz75SfHKd9In2C81bthiVOMWdsOARGPrrHYs=";
    public static final int EXPIRY_IN_SECONDS = 60;

    private static final User USER = User.builder().userId("hpotter").firstName("Harald").lastName("Töpfer").password("GoldenSnitch1991").build();

    private final JwtTokenService underTest = new JwtTokenService(SECRET, EXPIRY_IN_SECONDS);

    @Test
    void testOnlyRelevantDataIsEncoded() {
        String token = underTest.generateToken(USER);

        Jws<Claims> jws = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
        assertEquals(USER.getUserId(), jws.getBody().getSubject()); // user id
        assertTrue(jws.getBody().containsKey("iat")); // issue timestamp
        assertTrue(jws.getBody().containsKey("exp")); // expiry timestamp

        assertEquals(3, jws.getBody().keySet().size()); // no more
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 60, 3600, 86400})
    void testExpiryDateIsCorrectlySet(long expiryInSeconds) {
        JwtTokenService underTest = new JwtTokenService(SECRET, expiryInSeconds);
        String token = underTest.generateToken(USER);
        Jws<Claims> jws = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);

        Instant creation = jws.getBody().getIssuedAt().toInstant();
        Instant expiry = jws.getBody().getExpiration().toInstant();

        assertTrue(creation.isBefore(expiry));
        assertEquals(expiryInSeconds, Duration.between(creation, expiry).getSeconds());
    }

    @Test
    void testValidationIsSuccessful() {
        String token = underTest.generateToken(USER);

        assertEquals(USER.getUserId(), underTest.validateTokenAndGetUserId(token));
    }

    @Test
    void validationIsSuccessfulEvenIfTokenIsIssuedByAnotherServiceWithSameSecret() {
        String token = new JwtTokenService(SECRET, EXPIRY_IN_SECONDS).generateToken(USER);

        assertEquals(USER.getUserId(), underTest.validateTokenAndGetUserId(token));
    }

    @Test
    void testValidationFailsOnDifferentSecrets() {
        String secret = "4uv5rUFKzFxkpPHabG+0V9ROBxSXX6hrFPS77Lj+uSsocDeMtUDTxfAGEB7U9Ov6vhQEVFObw99d52cG4zm++Iw+p1tZiR9Cx0Nc4FW990TuOicnNreg7DccDbwE3zp68g9HnFYG/k0O1yUxbWGMkpuhknkPumLY71JE6+sfYl0=";
        String token = new JwtTokenService(secret, 60).generateToken(USER);

        assertNotEquals(secret, SECRET);
        assertNull(underTest.validateTokenAndGetUserId(token));
    }

    @Test
    void testValidationFailsIfExpired() {
        String expiredToken = new JwtTokenService(SECRET, -1).generateToken(USER);

        assertNull(underTest.validateTokenAndGetUserId(expiredToken));
    }

}

