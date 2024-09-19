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

    private static final long EXPIRATION_TIME = 60 * 60 * 1000;

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret) {
        this.secretKey = new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8),
            SIG.HS256.key().build().getAlgorithm()
        );
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

    public String createJwt(String email) {
        return Jwts.builder()
            .claim("email", email)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(secretKey)
            .compact();
    }
}
