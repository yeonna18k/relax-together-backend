package kr.codeit.relaxtogether.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final int ACCESS_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000;
    private static final int REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8),
            SIG.HS256.key().build().getAlgorithm()
        );
    }

    public String getType(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("type", String.class);
    }

    public String getEmail(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("email", String.class);
    }

    public Boolean ieExpired(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getExpiration()
            .before(new Date());
    }

    public String createAccessToken(String email) {
        return Jwts.builder()
            .claim("type", "access")
            .claim("email", email)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
            .signWith(secretKey)
            .compact();
    }

    public String createRefreshToken(String email) {
        return Jwts.builder()
            .claim("type", "refresh")
            .claim("email", email)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(secretKey)
            .compact();
    }

    public String createNewAccessToken(String refreshToken) {
        return Jwts.builder()
            .claim("type", "access")
            .claim("email", getEmail(refreshToken))
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
            .signWith(secretKey)
            .compact();
    }

    public String createNewRefreshToken(String refreshToken) {
        return Jwts.builder()
            .claim("type", "refresh")
            .claim("email", getEmail(refreshToken))
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
            .signWith(secretKey)
            .compact();
    }
}
